自动打包项目文件生成tar包

第一步：
  在app module下的build.gradle 文件里面添加任务
  
  ```
  task tarz(type: Tar) {
    archiveName = 'temp.txt'
    from 'src'

    destinationDir  file('build/tar')
    extension 'tar'
    compression = Compression.GZIP
}
```

第二步：gradle sync

第三步：找到gradle任务中others->tarz，并执行，会自动生成build/tar/temp.txt文件，复制到其他电脑，改名为app.tar.gz，解压后就是项目文件



