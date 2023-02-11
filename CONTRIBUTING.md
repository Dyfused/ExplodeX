# Contributing

As I am poor at English, so the contributing guide is writing in Chinese.

首先非常感谢你有兴趣来为这个项目做出贡献。但是我们仍然需要遵守一些规则，比如代码规范（codestyle）。

## 代码规范

### Kotlin

基本遵守 `Kotlin obsolete IntelliJ IDEA codestyle`，除了

- 使用 Tab 不使用空格（README 和 Contributing 里为了演示需要，可以使用空格）。
- 枚举（Enum）和常量命名遵守 `[A-Z][_a-zA-Z\d]*`，即使用大写驼峰或者大写下划线，例如 `AppleCompany` 或 `APPLE_COMPANY`
  均可。（特别强调，因为在新的 `Kotlin Coding Conventions` 里被修改了）
- `if`，`for`，`while`，`catch`，`when` 等关键字与后面的左括号 `(` 之间不要有空格，例如：
  ```
  if(true) {
  /* DO SOMETHING */
  }
  ```
- 注解（Annotation）和被标注的代码需要换行，例如：
  ```kotlin
  @PropertyName("Yee")
  private val a = 0
  ```
- 少用 Top Level Function，尽量限制其作用域。

### Java

和 `IntelliJ IDEA` 默认规范相同，除了

- 使用 Tab 不使用空格。
- `if`，`for`，`while`，`catch`，`when` 等关键字与后面的左括号 `(` 之间不要有空格。

### Markdown

尽量遵守 [`Google Markdown Style Guide`](https://google.github.io/styleguide/docguide/style.html)。

### 其他

看着现有代码抄，或者随意点也无所谓。