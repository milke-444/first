IOC / AOP 学习笔记
一、 IOC 容器与 Bean 管理
1. 容器核心接口
org.springframework.context.ApplicationContext 代表了 IoC 容器，它是 BeanFactory 的子接口。底层 Bean 的存取交给了内部的 BeanFactory 实现，而 ApplicationContext 还负责 Bean 的生命周期、事件发布等企业级功能。

2. BeanFactory 与 ApplicationContext 的区别
BeanFactory：只管 Bean 的管理注入，属于最基础的容器。
ApplicationContext：在 BeanFactory 基础上扩展了更多高级功能（如国际化、AOP 集成、事件机制等），更全面，所以一般开发中使用 ApplicationContext。
3. 不同 Bean 的配置方式
方式一：基于配置类的显式配置
在我们写配置类时会有这种方式：


@Configuration
public class AccountConfig {

    @Bean
    public AccountService accountService() {
        return new AccountService(accountRepository());
    }

    @Bean
    public AccountRepository accountRepository() {
        return new AccountRepository();
    }
}
@Configuration：表示这个文件里面存储了 Bean 的配置信息。
@Bean：注解表示该方法返回的是一个 Bean 对象。
但是我们发现这个方式，和我们常见的 Controller 等配置方式不同，这就引出了第二种。

方式二：基于 XML 的注解扫描（传统 Spring）
我们常见的 @Controller 等注解，在传统 Spring 中主要依靠 XML 配置。XML 中配置了对应的注解处理信息让 @Controller 等注解生效，然后通过 <context:component-scan> 去扫描对应包下有无这些注解，进行 Bean 的注入。

方式三：基于纯 Java 的组件扫描（Spring Boot 现代方式）
方式二中与我们现在使用的依然有很大区别，现在我们好像根本没用过所谓的 XML 去配置 Bean。这是因为目前通过 @ComponentScan 注解代替了 XML 自动扫描这些注解信息，让其生效并注入容器。（在 SpringBoot 中，@SpringBootApplication 注解内部默认包含了 @ComponentScan）。

4. 自动装配原理（核心）
4.1 自动装配机制的理解
目的：将一些开发常用的功能嵌入装配。例如封装了 Tomcat，以前运行 Web 项目需要先构建自己的 Tomcat，但现在使用 SpringBoot 就可以直接运行，就是因为 Tomcat 被封装到了容器中。
为什么使用自动装配：很多常用的功能虽然常用，但有些项目还是不使用的，如果全使用就会产生资源损耗。于是通过自动装配来达到“用什么，导入什么”的效果，让用户自己选又太麻烦。
条件注入：自动装配和我们自己写的功能交给容器管理是很像的，通过对特定路径的有条件注入，如使用 Tomcat 你的项目依赖中会有对应的类，容器通过条件词（条件注解）来自动判断并注入，这样即使不自己配置，也可使用其功能。
4.2 自动装配理解补充
什么是自动装配：我们使用 Maven 时发现有很多的 Jar 包，里面都有别人写好的功能。在以前使用时，我们需要对每个 Jar 包进行 Bean 配置后才能使用，但现在很多功能可以直接使用。一些如 Redis 的功能现在只需要写一段 POM 依赖就可以在容器中使用。所以自动装配就是简化我们使用第三方功能模块的复杂程度，让开发更简便。
底层实现：
通过 @EnableAutoConfiguration 注解实现，在运行时读取指定文件中的全类名。
版本差异：旧版读取 META-INF/spring.factories；最新版 Spring Boot 3.x 放在了 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 中。
装配过程：实际就是运行时自动读取要使用的 Jar 包下的全类名，通过反射实例化对应的功能类，然后像我们自己将代码模块注入容器一样，SpringBoot 统一把自动装配后的代码注入容器，让我们直接使用。
POM 与 YML 的分工：
POM 文件：通过 Maven 获取对应功能的 Jar 包（提供类的基础载体，没有 Jar 包就没有类）。
YML 文件：配置一些参数，配合条件注解生效。
条件注解（如 @ConditionalOnClass）：检查 Classpath 上有没有某个类（即有没有引入对应 Jar 包），来决定是否创建 Bean。只让满足条件的注入，否则全部注入会消耗大量内存。
配置属性绑定：@EnableConfigurationProperties 配合 @ConfigurationProperties，负责把 application.yml 中的配置值注入到自动装配生成的 Bean 中。
二、 AOP 学习
1. Spring 自带 AOP（传统方式）
配置通知时需实现 org.springframework.aop 包下的一些接口：

前置通知：MethodBeforeAdvice
后置通知：AfterReturningAdvice
环绕通知：MethodInterceptor
异常通知：ThrowsAdvice
2. 使用 AspectJ 实现切面（现代主流方式）
普通 POJO 的实现方式，配合 @Aspect 等注解使用。这是目前最常用的 AOP 实现方式，更简单，更好用。

3. AOP 底层原理：动态代理
在每个被截断的方法运行时，动态代理织入自己的 AOP 代码。实际运行效果 = 源代码 + AOP 代码。

理解动态代理
使用代理是为了解决什么问题：无侵入增强，即在不修改原来代码逻辑的基础上增加功能。

静态代理：
我们写了一个方法，它的输出结果是“狗”，但我们想让他多输出点内容，还不修改原来的方法。这里有点像是函数的调用，输出“狗”的方法是一个函数，我们再创建一个输出“猫”的方法，并且调用这个“狗”的函数，输出的结果不就是“狗加猫”，实现了无侵入增强。但是如果功能多了，就需要写多个“猫”的方法，会很冗余，于是便引出了动态代理。
动态代理的两种方式：
JDK 动态代理：
原理：为我们要增强的方法创建一个实现相同接口的代理对象，我们看到的增强后的效果是代理方法实现的。
局限：目标对象必须实现接口，否则无法使用。
底层：JDK 代理是在运行时动态生成一个代理类，代理类实现了相同接口，并在内部通过反射调用目标对象（如 realDog）的方法来完成增强。类似于：Dog realDog = new ZhtyDog();，代理对象持有 realDog 的引用并在前后加逻辑。
CGLIB 动态代理：
原理：让代理对象继承目标对象，通过调用父类方法（super），并在前后增加自己的业务来达成增强。
局限：目标类不能是 final 修饰的。
总结：这两种在实现上很相似，都是通过不同手段（接口实现 vs 继承）达到调用原方法并在前后增加逻辑的目的。
4. AOP 常见应用场景
基础日志：通过前置通知、后置通知、环绕通知、异常通知实现，在对应代码运行到指定节点时，记录日志提示。
事务管理：通过环绕通知实现事务的开启、提交与回滚。
权限校验：通过前置通知在方法执行前校验权限。
