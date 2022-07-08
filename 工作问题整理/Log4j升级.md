# Log4j升级

​		

​		公司老项目采用log4j1.2.8+commons-logging 进行日志输出。升级改造需要将log4j升级至2.3.2版本。

1. jar包升级替换

   1. 删除原log4j包   log4j-1.2.8.jar

   2. 下载2.3.2版本https://dlcdn.apache.org/logging/log4j/2.3.2/apache-log4j-2.3.2-bin.zip

      将以下四个包加入项目中

      log4j-1.2-api-2.3.2.jar

      log4j-api-2.3.2.jar

      log4j-core-2.3.2.jar

      log4j-jcl-2.3.2.jar    ##整合commons-logging

2. 配置文件变更

   1. 删除原log4j的配置文件

   2. 编写log4j2.xml

      ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      
      <configuration status="WARN">
          <!-- OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
          <!--先定义所有的appender-->
          <appenders>
              <!--这个输出控制台的配置-->
              <Console name="Console" target="SYSTEM_OUT">
                  <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
                  <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
                  <!--这个都知道是输出日志的格式-->
                  <PatternLayout charset="UTF-8" pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] [%c] [%M]%m%n"/>
              </Console> 
              <!--这个会打印出所有的信息 达到50MB 或者超过12小时会自动切分日志，形成备份文件-->
              <RollingFile name="logFile" fileName="/log/all.log"
                           filePattern="/log/all-%d{MM-dd-yyyy HH}-%i.log">
                  <PatternLayout charset="UTF-8"
                                 pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] [%c] [%M]%m%n"/>
      	    	<Policies>
                  	<TimeBasedTriggeringPolicy interval="12"/>  <!--12小时 与filePattern表达式相关 -->
      	            <SizeBasedTriggeringPolicy size="50MB"/>
          	    </Policies>
              </RollingFile>
                
              <RollingFile name="errorFile" fileName="/log/error.log"
                           filePattern="/log/error-%d{MM-dd-yyyy HH}-%i.log">
                  <PatternLayout charset="UTF-8"
                                 pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] [%c] [%M]%m%n"/>
                  <Filters>
                      <!-- 只记录error级别信息 -->
                      <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
                  </Filters>
      	    	<Policies>
                  	<TimeBasedTriggeringPolicy interval="12"/>  <!--12小时 与filePattern表达式相关 -->
      	            <SizeBasedTriggeringPolicy size="50MB"/>
          	    </Policies>
              </RollingFile>
          </appenders>
          <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
          <loggers>
              <!--建立一个默认的root的logger-->
              <root level="trace">
              </root>
              
              <logger name="com.chen.common" level= "info">
                  <AppenderRef ref="logFile"/>
              </logger>
              
          </loggers>
      </configuration>
      ```

      