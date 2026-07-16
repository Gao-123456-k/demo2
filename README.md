# Weather CLI

一个简单的命令行天气查询工具。

## 功能

- `help` - 显示帮助信息
- `version` - 显示版本
- `status` - 显示程序状态
- `weather <city>` - 查询当前天气
- `forecast <city> [days]` - 查询天气预报（默认7天，最多16天）
- `exit` / `quit` - 退出程序

## 安装和运行

### 前提条件

- Java 25 或更高版本
- Maven 3.9+（用于构建）

### 构建项目

```bash
cd weather-cli
mvn clean package
```

### 运行程序

```bash
java -jar target/weather-cli-1.1.0.jar
```

或者直接双击 `run.bat`

## 天气API

本工具使用 [Open-Meteo](https://open-meteo.com/) API 获取天气数据，**免费、无需API Key、国内可访问**。

## 示例

```
=== Weather CLI v1.1.0===
Type 'help' for available commands.

weather> weather Beijing
Weather in Beijing:
  Description: Partly cloudy
  Temperature: 32.4°C
  Feels Like: 36.4°C
  Humidity: 47%
  Wind Speed: 4.1 km/h

weather> forecast Tokyo 3
3-Day Forecast for Tokyo, Japan:
Date         Weather             High      Low   Rain(mm) Wind(km/h)
---------------------------------------------------------------------------
2026-07-16   Drizzle            33.4°C    24.5°C      0.1mm      5.9
2026-07-17   Drizzle            29.9°C    25.3°C      1.1mm      6.6
2026-07-18   Partly cloudy      29.1°C    24.6°C      0.0mm      6.4

weather> version
Weather CLI v1.1.0
```

## 错误处理

程序会捕获并显示以下错误：
- 未指定城市名称
- 城市未找到
- 网络连接问题
- 无效的天数参数

## 项目结构

```
weather-cli/
├── pom.xml
├── README.md
├── .gitignore
├── run.bat
├── test.bat
└── src/
    └── main/
        └── java/
            └── com/
                └── weather/
                    ├── Main.java
                    ├── CommandHandler.java
                    ├── WeatherService.java
                    └── WeatherException.java
```
