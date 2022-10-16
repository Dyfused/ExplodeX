# SimpleGQL

用来代理请求的 GraphQL 工具。

## 示例

```kotlin
val query = SingleQuery("query { hello }").send<GraphQLDataR<What>>("http://example-server.com:10443/graphql", "my-token-is-here")
val data: What = query.data.r
```