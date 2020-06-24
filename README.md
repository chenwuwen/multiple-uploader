### Multiple-uploader

#### 1.功能描述
>**Multiple-uploader**是一个Java上传工具,他最大的特点是可以通过简单的方式，将文件上传到多个CDN存储中(通过适配,也可以上传到其他存储)
,通过添加多个依赖,将文件上传到不同的位置,用于实现灾备和负载,同时当需要更改存储时,也可以很方便的进行实现的替换。
需要注意的是,目前只支持最简单的上传操作。不支持其他操作

#### 2.如何获得
~~如果需要获取快照版本,需要先添加仓库地址~~  已发布到[中央仓库](https://mvnrepository.com/chenwuwen)
```xml
<repositories>
 
<repository>
    <id>snapshots</id>
    <name>snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
 
</repositories>
```
添加完仓库后,就可以添加依赖了。

```xml
<dependency>
  <groupId>io.github.chenwuwen</groupId>
  <artifactId>multiple-uploader-api</artifactId>
  <version>${version}</version>
</dependency>

<!--七牛云适配器-->
<dependency>
  <groupId>io.github.chenwuwen</groupId>
  <artifactId>multiple-uploader-qiniu</artifactId>
  <version>${version}</version>
</dependency>

<!--又拍云适配器-->
<dependency>
  <groupId>io.github.chenwuwen</groupId>
  <artifactId>multiple-uploader-upyun</artifactId>
  <version>${version}</version>
</dependency>

```

- **注意** ：multiple-uploader-api是必须的,其他的是非必须的,可以依赖一个,也可以依赖多个
也可以都不依赖,如果不依赖适配器,则默认上传操作是空操作,如果选择了其中的一个或者多个依赖,则还需要
自行添加真实上传依赖。

`例：`
选择了multiple-uploader-qiniu依赖,此时打开[七牛云开发者中心](https://developer.qiniu.com/kodo/sdk/1239/java)
可以看到,使用七牛云需要添加哪些SDK,那么需要将这些依赖添加进你的依赖管理中去,如果未添加,则编译不通过


#### 3.如何使用
获得依赖之后,首先需要在classpath 下创建storage.properties文件,[这里](https://github.com/chenwuwen/multiple-uploader/blob/master/storage.properties)
这个文件下的配置,是会在初始化Uploader下进行读取的。

配置完成后,即可在类/方法中通过

```java
//初始化也可以放在类的静态成员变量中,例如：private static final Uploader uploader = UploaderFactory.getUploader();
Uploader uploader = UploaderFactory.getUploader();
uploader.push("sourcePath","targetPath");
```

这样就可以上传了

- 使用回调
除了正常上传外还可以进行上传结果回调
```java
        Uploader uploader = UploaderFactory.getUploader();
        uploader.push("sourcePath", "targetPath", new PushCallback() {
            @Override
            public void onSuccess(String sourcePath, String targetPath) {
                String targetStorageName = getTargetStorageEnum().toString();
                System.out.println(targetStorageName + "====》成功");
            }

            @Override
            public void onError(String sourcePath, String targetPath, Throwable throwable) {
                String targetStorageName = getTargetStorageEnum().toString();
                System.out.println(targetStorageName + "====》失败");
            }
        });
```

使用回调需要继承PushCallback抽象类,并实现它的onSuccess/onError 方法,需要注意的是
在该方法中可以调用getTargetStorageEnum()方法,该方法返回当前CDN(或者其他存储)的枚举对象
此时就可以通过switch case来对具体存储做具体实现了,onSuccess/onError 方法可能会执行多次,具体就取决你
要往哪些存储上存储了。
```java
    @Override
    public void onSuccess(String sourcePath, String targetPath) {
        ActualCloud actualCloud = getTargetStorageEnum();
        switch (actualCloud) {
            case QINIU_CLOUD:
                service.print();
                break;
            case UP_CLOUD:
                service.print1();
                break;
            default:
                System.out.println("发生了什么");
        }
    }
```

> **注意：**上传操作是IO操作,因此当文件较大,会比较耗时,如果需要上传到多个存储,更是雪上加霜，因此可以使用**异步上传**。

```java
Uploader uploader = UploaderFactory.getUploader();
uploader.asyncPush("sourcePath","targetPath");
```
这样就完成了异步上传操作,同样的,异步上传也可以进行回调操作。
有一点与同步上传不同的是,异步上传还可以监听,上传结束事件。
```java
        Uploader uploader = UploaderFactory.getUploader();
        uploader.asyncPush("sourcePath", "targetPath", new CompleteCallback("sourcePath","targetPath") {
            @Override
            public void handler(String sourcePath, String targetPath) {
                System.out.println("上传任务完成");
            }
        });
```
实现上传结束监听,需要继承CompleteCallback抽象类,并实现handler()抽象方法,需要注意的是,这个事件在一次上传中,一定会实现一次,且只有一次
