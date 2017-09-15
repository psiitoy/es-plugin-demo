## elasticsearch2.1 插件开发demo
 
> 本项目例举了3个 `es2.1`插件开发的demo，均为1分钟入门。

- `plugins`包下面
  + `filter` 一个基于`ActionFilter`开发拦截器类型(日志、监控等)的`Plugin`的例子 
  + `simpledemo` 一个Rest插件的简单例子
  + `slowlog` 慢日志相关插件(不完整的部分插件内容)
  
* 我们重点关注前两个插件。

* `pom`文件针对的是`ActionFilter`的插件，如果是发行其他插件需要替换相关配置项。

* `es`插件开发过程参见:[[源码]Elasticsearch源码5(2.x插件开发)](https://psiitoy.github.io/2017/08/12/[%E6%BA%90%E7%A0%81]Elasticsearch%E6%BA%90%E7%A0%815(2.x%E6%8F%92%E4%BB%B6%E5%BC%80%E5%8F%91)/)