# EazyWrite

## 配置本项目
修改此文件
```shell
app/src/main/java/com/eazywrite/app/data/network/Network.kt
```
```kotlin
object Network {

   // 服务端 baseUrl
   private const val baseUrl = "https://easywrite.wilinz.com/"
   //    private const val baseUrl = "http://192.168.1.5:10010/"

   //    val openaiUrl = "${baseUrl}openai/v1/"
   // See https://www.ohmygpt.com/
   private const val openaiUrl = "https://c-z0-api-01.hash070.com"

   // 必选
   private val openaiKey = Base64.decode("api key base64").toString(Charsets.UTF_8) // TODO

   // 可选
   private const val textinAppId = ""

   // 可选
   private const val textinSecretCode = ""
   
   //...
}
```
## 手动合并Git:

```shell
    # 拉取原仓库代码
    git remote add upstream 要合并的仓库地址
    git pull upstream
    # 将原仓库代码合到自己仓库
    git merge upstream/dev
    # 推送到自己仓库
    git push
```

## Github Action 云编译：

1. Fork 本仓库
2. 在自己仓库的 Settings -> Secrets and variables -> Secrets -> Action 中添加 (New repository secret) 以下 Secrets
   1. ANDROID_KEY_BASE64   # jks 证书文件的 base64 编码字符串
   2. ANDROID_KS_PASS   # 证书密码
   3. ANDROID_KEY_ALIAS  # 证书别名
3. 添加完毕后，点击 Actions -> Android Release -> Run workflow 运行一次
4. 等待编译完成后，点击 Actions -> Android Release ，下载 Artifacts 下面编译好的 apk 文件
5. 解压下载的 *.apk.zip 文件, 安装运行

## 文件转 base64
### PowerShell:
```shell
# 将文件转换为base64编码并输出到控制台：
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\path\to\file.txt"))
```
### Linux, MacOS:
```shell
# 将文件转换为base64编码并输出到控制台：
base64 /path/to/file.txt
```
在上述命令中，你需要将 /path/to/file.txt 替换为实际的文件路径。

## 创建 Android 签名证书教程
1. 打开Android Studio，并进入你的项目。

2. 选择菜单栏的Build，然后选择Generate Signed Bundle/APK。

3. 在弹出的窗口中，选择您要打包的模块。如果您还没有配置签名证书，请单击Create New进行配置。

4. 在Key store path字段中，单击右侧的…并选择保存证书的位置。然后为证书指定一个名称。

5. 在Key store password字段中输入密码，并确认。

6. 输入Certificate信息，包括有效日期、姓名、单位、城市、州/省和国家/地区。

7. 输入Key信息，包括别名、密码、有效日期、姓名、单位、城市、州/省和国家/地区。

8. 单击确认，稍等片刻，Android Studio就会生成一个签名证书。
