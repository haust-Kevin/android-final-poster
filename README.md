# Poster

安卓大作业，一个手机端的接口测试工具，仿照 postman，基于腾讯的[QMUI](https://qmuiteam.com/android)组件库

HTTP库使用OKHTTP


能够在手机上进行不是很复杂的API测试，具有比较大的实用价值

同时可以作为一个网络请求工具，可以简化每天打开指定网站或APP进行的重复工作(例如：签到，打卡，报平安等)

#### 请求集
 - 支持默认站点 （含有HTTP/HTTPS选择）
 - 支持启用 Session
  
#### 请求
 - GET/POST
 - 支持请求参数  
 - 支持请求体 （仅支持JSON请求体）
 - 支持添加请求头
 - 支持restful path参数  
 &emsp;url path里直接用 `/prefix/path/:sample_param/suffix/path`  
 &emsp;在params里可以直接 用`sample_param=sample_value`  
 &emsp;app会自动判断是否作为restful参数并进行填充

#### 响应体
 - 请求报文
 - 响应报文
 - 响应体（仅支持JSON格式化，不支持二进制文件预览）

#### 注意
 - 如果API不支持HTTP，请使用HTTPS协议，避免重定向造成POST失效，因为重定向会强制变成GET请求


  

# release

[app-release.apk](https://github.com/haust-Kevin/Poster/raw/main/app/release/app-release.apk)


---


如发现BUG，还请在 issues 里提出，我一定会及时给予回应

