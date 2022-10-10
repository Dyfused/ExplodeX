package explode2.simplegql

data class RData<T>(val r: T)

typealias GraphQLDataR<T> = GraphQLData<RData<T>>