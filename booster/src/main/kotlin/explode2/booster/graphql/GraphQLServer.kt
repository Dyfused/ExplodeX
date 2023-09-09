@file:Suppress("DEPRECATION")

package explode2.booster.graphql

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import explode2.booster.graphql.MazeProvider.Companion
import explode2.booster.graphql.definition.ExplodeMutation
import explode2.booster.graphql.definition.ExplodeQuery
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.execution.*
import graphql.language.SourceLocation
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.io.IOException
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal val logger = LoggerFactory.getLogger("GraphQL")

val graphQLServer = GraphQLServer<ApplicationCall>(
	Companion.getProvider().query,
	Companion.getProvider().mutation,
	{ it.receiveText() },
	{ mapOf("token" to (it.request.header("x-soudayo") ?: "trash-potato-server")) }
)

class GraphQLServer<T>(
	query: ExplodeQuery,
	mutation: ExplodeMutation,
	val requestContentReader: suspend (T) -> String,
	val contextMapGenerator: suspend (T) -> Map<Any, Any>,
) {

	object NonNegativeIntUnBoxer : ValueUnboxer {
		override fun unbox(boxedValue: Any?): Any? {
			return if(boxedValue is NonNegativeInt) {
				boxedValue.value
			} else {
				boxedValue
			}
		}
	}

	val mapper = jacksonObjectMapper()

	private val dataFetcherExceptionHandler = object : DataFetcherExceptionHandler {
		val marker = MarkerFactory.getMarker("DataFetcher")

		@Suppress("OVERRIDE_DEPRECATION")
		override fun onException(ctx: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
			val exception = ctx.exception
			val source = ctx.sourceLocation
			val path = ctx.path

			if(exception !is DataFetchException) {
				logger.warn(marker, "Exception occurred on data fetching", exception)
			} else {
				if(exception.cause != null) {
					logger.debug(marker, "Exception occurred on data fetching: ${exception.message}", exception.cause)
				} else {
					logger.debug(marker, "Exception occurred on data fetching: ${exception.message}")
				}
			}

			val ex = ExplodeExceptionWhileDataFetching(path, exception, source)

			return DataFetcherExceptionHandlerResult.newResult().error(ex).build()
		}
	}

	private class ExplodeExceptionWhileDataFetching(
		path: ResultPath,
		exception: Throwable,
		sourceLocation: SourceLocation
	) : ExceptionWhileDataFetching(path, exception, sourceLocation) {
		override fun getMessage(): String {
			return exception.message ?: super.getMessage()
		}
	}

	private val customSchemaGeneratorHooks = object : SchemaGeneratorHooks {
		override fun willGenerateGraphQLType(type: KType): GraphQLType? = when(type.classifier as? KClass<*>) {
			UUID::class -> ExtendedScalars.UUID
			OffsetTime::class -> ExtendedScalars.Time
			OffsetDateTime::class -> ExtendedScalars.DateTime
			NonNegativeInt::class -> ExtendedScalars.NonNegativeInt
			else -> null
		}
	}

	private val schema = toSchema(
		SchemaGeneratorConfig(supportedPackages = listOf("explode2.booster.graphql.definition"), hooks = customSchemaGeneratorHooks),
		listOf(TopLevelObject(query)),
		listOf(TopLevelObject(mutation))
	)

	private val graphQL: GraphQL = GraphQL.newGraphQL(schema)
		.defaultDataFetcherExceptionHandler(dataFetcherExceptionHandler)
		.valueUnboxer(IDValueUnboxer())
		.valueUnboxer(NonNegativeIntUnBoxer)
		.build()

	private val requestParser = object : GraphQLRequestParser<T> {
		override suspend fun parseRequest(request: T): GraphQLServerRequest? {
			return try {
				mapper.readValue(requestContentReader(request), GraphQLServerRequest::class.java)
			} catch(ex: Exception) {
				throw IOException("Unable to read GraphQL payload", ex)
			}
		}
	}

	private val contextFactory = object : GraphQLContextFactory<GraphQLContext, T> {
		override suspend fun generateContextMap(request: T): Map<Any, Any> {
			return contextMapGenerator(request)
		}
	}

	private val requestHandler = GraphQLRequestHandler(graphQL, KotlinDataLoaderRegistryFactory())

	private val graphQLServer = GraphQLServer(requestParser, contextFactory, requestHandler)

	suspend fun handle(request: T): String? {
		val result = graphQLServer.execute(request)
		return result?.let { mapper.writeValueAsString(result) }
	}

	fun getSchema() = schema

}