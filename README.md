# Poster

安卓大作业，一个手机端的接口测试工具，仿照 postman，基于腾讯的[QMUI](https://qmuiteam.com/android)组件库
能够在手机上进行不是很复杂的API测试，具有比较大的实用价值
同时可以作为一个网络请求工具，不再需要每天打开指定网站或APP进行重复工作

添加请求集
 - 支持默认站点 （含有HTTP/HTTPS选择）
 - 支持启用 Session
  
添加请求
 - GET/POST
 - 支持请求参数
 - 支持请求体
 - 支持添加请求头
 - 支持restful path参数

响应体
 - 请求报文
 - 响应报文
 - 响应体（仅支持JSON格式化，不支持二进制文件预览）

注意
 - 如果API不支持HTTP,请使用HTTPS协议，避免重定向造成POST失效，重定向会强制变成GET请求

HTTP库使用OKHTTP
  

# release

[app-release.apk](https://github.com/haust-Kevin/Poster/raw/main/app/release/app-release.apk)
