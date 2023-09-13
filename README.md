# BurpDnsCache
Modify DNS resolution rules in BurpSuite
# 前言
在工作中需要频繁切换hosts变换Dns的解析记录，但是由java写的burpSuite工具本身会对dns解析记录进行缓存，导致每次切换hosts后都需要重启burpSuite（或者等几分钟缓存被清除），非常浪费时间且麻烦。
当然也尝试过通过修改 java的JVM DNS缓存配置（openjdk 15 - burp2021，openjdk1.8 - burp2.1.06）:
- 应用启动时, 设置启动参数例如  `-Dnetworkaddress.cache.ttl=0 -Dnetworkaddress.cache.negative.ttl=0`
- 设置JVM配置，例如编辑$JRE_HOME/lib/security/java.security 文件,   `networkaddress.cache.negative.ttl=0`  `networkaddress.cache.ttl=0`
当时均未成功，故想到了在BurpSuite中编写一款插件，修改Dns解析记录，也不需要在机器上直接修改hosts了。
# 使用说明
1. burpsuite -> Extender -> Extensions -> Add ,然后在Extension File处选中此jar文件![image](https://github.com/mayi077/BurpDnsCache/assets/71206205/10c66f7e-8b8b-44b7-9c9f-5102a6dcce24)
2. 随后菜单栏会出现 DNS Cache 这个Tab，只需在此填写host后点击Submint即可 ![image](https://github.com/mayi077/BurpDnsCache/assets/71206205/d58eb58f-4501-4c7d-aab0-9a44d6e03098)
3. 随后我们通过burpsuite访问 abc.com 看到解析ip确实为1.1.1.1了 ，设置成功 ![image](https://github.com/mayi077/BurpDnsCache/assets/71206205/bb555502-b7a1-450e-9139-4e37eba31300)
4. submint成功后，若想重新修改需点击 Edit后重新提交，若想重置也需点击Reset后重置

**其他补充：**
1. host输入时支持 `#` 号注释，支持无效空行，支持一个ip对应多个域名 
2. 此配置属于burpsuite的项目级，新项目需重新配置

# 后续想法
1. 支持创建 host 记录标签，方便来回切换而不需重复 复制粘贴
2. 支持从远程拉取 host 信息来创建host记录标签
   
