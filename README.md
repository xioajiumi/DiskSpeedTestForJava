# DiskSpeedTestForJava

## 1.What for?

有时候你想知道程序运行环境的硬盘的读写速度，但不能直接使用diskSpd或者fio或者是dd这类工具来进行测试，那么这个纯Java程序就可以帮助你了。
这款插件使用了NIO和多线程技术，能很好的测试出硬盘的读写速度与iops，同时占用的cpu资源也很少。
Sometimes, you want to know the read and write speed of the disk in your program’s running environment, but you can't
directly use tools like diskSpd, fio, or dd for testing. This pure Java program can help you with that.
This plugin uses NIO and multi-threading technologies to accurately test the disk's read/write speed and IOPS, while
consuming very little CPU resources.

## 2.How to use?

### 2.1.Download

具体使用方法参考`Demo.java`
For detailed usage, refer to `Demo.java`

## 3.Important Notice

这个插件目前无法绕过操作系统的文件系统缓存，所以测试结果无法与fio这些更强大、更专业的工具相媲美，尤其是读取速度，可能会是写入速度的几十倍。
运行时请确保你的硬盘有足够的空间（默认测试文件占用不到4G，当然，具体取决于你如何设置）。
Currently, this plugin cannot bypass the operating system's file system cache, so the test results may not compare to
more powerful and professional tools like fio, especially for read speed, which could be tens of times faster than write
speed.
Please ensure that your disk has sufficient space for the test (the default test file takes up less than 4GB, although
this depends on your configuration).

## 4.Future Plan

- [ ] 优化代码，提高性能
- [ ] 使用JNI等技术绕过文件系统缓存（可以考虑借用[jaydio](https://github.com/smacke/jaydio)
  和 [kdio](https://github.com/lexburner/kdio)）
- [ ] Optimize code to improve performance
- [ ] Use JNI or other technologies to bypass the file system cache (consider
  leveraging [jaydio](https://github.com/smacke/jaydio) and [kdio](https://github.com/lexburner/kdio))