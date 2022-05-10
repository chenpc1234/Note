# 条件Shell脚本

## 简单demo

- 查看shell解析器    echo $SHELL
- 执行脚本
  - 使用 sh + 脚本的相对路径或绝对路径
  - 使用 bash +脚本的相对路径或绝对路径
  - 脚本的相对路径或绝对路径  需要具有执行权限 ：chmod +x helloworld.sh

- 写个“helloword”到文件中

  ```sh
  #!/bin/bash
  cd /root/shelltest
  touch hellotest.txt
  echo "hello word" >>hellotest.txt
  echo "success"
  ```

## shell变量

## 系统变量

## 自定义变量

```bash
A=2             #定义变量  =前后不能后空格！！！
echo $A         #输出A    
unset A         #解除变量
readonly B=2    #自定义只读变量B=2  不能unset

#bash中 默认都是字符串，无法进行运算
C=1+1
echo $C      # 输出 1+1

#变量中含有空格，无论开头还是中间，必须使用单引号或双引号括起来
D="nihao hello word"

#声明的变量为局部变量 用export升级级为全局变量 不然其他shell无法使用  !!!
export D 
```

### 特殊变量

- $0               代表该脚本名称，

- $1-$9         代表第一到第九个参数，十以上的参数，十以上的参数需要用大括号包含，如${10}

- $#              表示输入参数的个数   经常用于循环

- $*              命令行中所有的参数，$* 把所有的参数看成一个整体

- $@             命令行中所有的参数，$@把每个参数区分对待

- $?                判断上条命令是否执行成功 0   执行成功    非0 执行失败```sh

- demo如下

  ```bash
  [root@cluster5 shelltest]# vi parameter.sh 
  [root@cluster5 shelltest]# more parameter.sh 
  #!/bin/bash
  echo &quot;$0 $1 $2 $3&quot;
  echo $#
  echo $*
  echo $@
  
  [root@cluster5 shelltest]# sh parameter.sh 111 222 333
  parameter.sh 111 222 333
  3
  111 222 333
  111 222 333
  ```

## 运算符

- 基本语法“$((运算式))” , 最常用
- expr  + , - , *,  /,  %    加，减，乘，除，取余#$[表达式]

```
[root@cluster5 shelltest]# echo $[(2+3)*4]
20
#$((表达式))
[root@cluster5 shelltest]# echo $(((2+3)*4))
20

#expr 运算符两端必须含有空格
[root@cluster5 shelltest]# expr 2+3
2+3
[root@cluster5 shelltest]# expr 2 + 3
5
#运算 一个expr运算表达式两测使用飘号``括起来  和平常的()左右一样
[root@cluster5 shelltest]# expr `expr 2 + 3` \* 2
10
```

## 条件判断

### **基本语法**

​		[ condition ]（注意condition前后要有空格），注意：条件非空即为true，[ atguigu ]返回true，[] 返回false。

- 常用判断条件两个整数之间比较 。
  -  =   字符串比较

  - -lt 小于（less than）

  - -le 小于等于（less equal）

  - -eq 等于（equal）

  - -gt 大于（greater than）

  - -ge 大于等于（greater equal）   

  - -ne 不等于（Not equal）

    ```bash
    [root@cluster5 shelltest]# [ 2 -lt 3 ]
    [root@cluster5 shelltest]# echo $?
    0
    ```

- 按照文件权限进行判断

  - -r 有读的权限（read）

  - -w 有写的权限（write）

  - -x 有执行的权限（execute）

    ```bash
    [root@cluster5 shelltest]# ls -l
    total 16
    -rw-r--r--. 1 root root 99 Apr  8 19:13 hellotest.sh
    [root@cluster5 shelltest]# [ -r hellotest.sh ]
    [root@cluster5 shelltest]# echo $?
    0
    [root@cluster5 shelltest]# [ -x hellotest.sh ]
    [root@cluster5 shelltest]# echo $?
    1
    ```

- 按照文件类型进行判断

  - -f  文件存在并且是一个常规的文件（file）

  - -e 文件存在（existence）

  -  -d 文件存在并是一个目录（directory）

    ```bash
    [root@cluster5 shelltest]# [ -f hellotest.sh ]
    [root@cluster5 shelltest]# echo $?
    0
    [root@cluster5 shelltest]# [ -d hellotest.sh ]
    [root@cluster5 shelltest]# echo $?
    1
    ```

### 多条件判断

- && 表示前一条命令执行成功时才执行后一条命令

- || 表示上一条命令执行失败后才执行下一条命令

  ```bash
  [root@cluster5 shelltest]# [ -d hellotest.sh ] && echo ok
  [root@cluster5 shelltest]# [ -d hellotest.sh ] || echo ok
  ok
  ```

## 流程控制

### IF 判断

#### 语法

```tex
if [ 条件判断式 ];then 
  程序 
fi 
--------------或者 -------------
if [ 条件判断式 ] 
  then 
    程序
elif [ 条件判断 ]
  then
    程序
fi
```

#### 注意事项：

- [ 条件判断式 ]，中括号和条件判断式之间必须有空格
- if后要有空格

#### demo

```bash
[root@cluster5 shelltest]# more if.sh 
#!/bin/bash
if [ $1 -eq "1" ]
 then
    echo "第一个参数是1"
elif [ $1 -eq "2" ]
 then
    echo "第1个参数是2"
fi

[root@cluster5 shelltest]# sh if.sh 1
第一个参数是1
[root@cluster5 shelltest]# sh if.sh 2
第1个参数是2
```

### case 判断

#### 语法

```tex
case $变量名 in 
  "值1"） 
    如果变量的值等于值1，则执行程序1 
    ;; 
  "值2"） 
    如果变量的值等于值2，则执行程序2 
    ;; 
  …省略其他分支… 
  *） 
    如果变量的值都不是以上的值，则执行此程序 
    ;; 
esac
```

#### 注意事项

- case行尾必须为单词“in”，每一个模式匹配必须以右括号“）”结束。
- 双分号“;;”表示命令序列结束，相当于java中的break。
- 最后的“*）”表示默认模式，相当于java中的default。

#### demo

```bash
[root@cluster5 shelltest]# more case.sh 
#!/bin/bash
case $1 in
"1")
    echo "输入的参数是1"
;;
"2")
    echo "输入的参数是2"
;;
*)
    echo "输入的参数不是1也不是2"
esac
[root@cluster5 shelltest]# sh case.sh 1
输入的参数是1
[root@cluster5 shelltest]# sh case.sh 2
输入的参数是2
[root@cluster5 shelltest]# sh case.sh 3
输入的参数不是1也不是2
```

### for 循环

#### 语法

```tex
for (( 初始值;循环控制条件;变量变化 )) 
 do 
  程序 
 done
--------或者-------------------

for 变量 in 值1 值2 值3… 
  do 
    程序 
  done
```

#### demo计算1-100 的和

```bash
[root@cluster5 shelltest]# more for1-100.sh 
#!/bin/bash
sum1=0
sum2=0
for((i=1;i<=100;i++))
do
    sum1=$[$sum1 + $i]
    sum2=`expr $sum2 + $i`
done
echo $sum1
echo $sum2
[root@cluster5 shelltest]# sh for1-100.sh 
5050
5050
```

#### demo循环打印输入参数

```bash
[root@cluster5 shelltest]# more forin.sh 
#!/bin/bash

for i in $*
do
    echo "!输入的参数是 $i"
done

for i in $@
do
    echo "@输入的参数是 $i"
done
[root@cluster5 shelltest]# sh forin.sh 1 2 3
!输入的参数是 1
!输入的参数是 2
!输入的参数是 3
@输入的参数是 1
@输入的参数是 2
@输入的参数是 3
#-------------------------------------------------------------------------
#注意！！！ $@ 必定是多个参数  $* 在加上"" 时会成为一个整体 
#-------------------------------------------------------------------------
[root@cluster5 shelltest]# more forin2.sh 
#!/bin/bash

for i in "$*"
do
    echo "!输入的参数是 $i"
done

for i in "$@"
do
    echo "@输入的参数是 $i"
done
[root@cluster5 shelltest]# sh forin2.sh 1 2 3
!输入的参数是 1 2 3
@输入的参数是 1
@输入的参数是 2
@输入的参数是 3
```

### while循环

#### 语法

```tex
while [ 条件判断式 ] 
  do 
    程序
  done
```

#### demo  1-100求和

```bash
[root@cluster5 shelltest]# more while1-100.sh 
#!/bin/bash
i=1
sum=0
while [ $i -le 100 ]
do
    sum=$[$sum + $i]
    i=$[$i + 1]
done
echo $i
echo $sum
[root@cluster5 shelltest]# sh while1-100.sh 
101
5050
```

## read 提示函数

### 语法

```tex
read -t 秒 -p "提示信息" 变量名

-p：指定读取值时的提示符；
-t：指定读取值时等待的时间（秒）
```

### demo

   10秒内输入名字 不然结束脚本

```bash
[root@cluster5 shelltest]# more read.sh 
#!/bin/bash
read -t 10 -p "请在10s内输入你的名字: " NAME
echo "你的名字是 $NAME"
[root@cluster5 shelltest]# sh read.sh 
请在10s内输入你的名字: ccc
你的名字是 ccc
```

## 函数

### 系统函数

#### basename  取文件名称

```bash
/root/shelltest/read.sh

[root@cluster5 shelltest]# basename /root/shelltest/read.sh 
read.sh
```

#### dirname  取文件路径

```bash
[root@cluster5 shelltest]# dirname /root/shelltest/read.sh 
/root/shelltest
```

### 自定义函数

#### 语法

```tex
function funname()
{
    程序
}
funname
```

#### 注意事项

- 必须在调用函数地方之前，先声明函数，shell脚本是逐行运行。不会像其它语言一样先编译。
- 函数返回值，只能通过$?系统变量获得，可以显示加：return返回，如果不加，将以最后一条命令运行结果，作为返回值。return后跟数值n(0-255)

#### demo计算两个输入参数的和

```bash
[root@cluster5 shelltest]# more fun.sh 
#!/bin/bash

#定义求和函数 sum
function sum()
{
        s=0
        s=$[$1 + $2]
        echo "$s"
}
#提示用户输入参数
read -t 10 -p "请输入第一个参数" p1
read -t 10 -p "请输入第二个参数" p2
#执行函数
sum $p1 $p2

[root@cluster5 shelltest]# sh fun.sh 
请输入第一个参数1
请输入第二个参数3
4

###############################################
#####包含return 返回值的可以使用 $? 取返回值
################################################

[root@cluster5 shelltest]# vi f.sh 

[1]+  Stopped                 vi f.sh
[root@cluster5 shelltest]# more f.sh 
#!/bin/bash

function ss()
{
    echo "sssss"
    return "3"
    echo "bbbb"
}
ss
[root@cluster5 shelltest]# sh f.sh 
sssss
[root@cluster5 shelltest]# echo $?
3
```

## 文本操作

### cut 剪切

​	cut 命令从文件的每一行剪切字节、字符和字段并将这些字节、字符和字段输出

#### 语法

```tex
cut [选项参数]  filename
-f 列号，提取第几列   列号(1,2 表示1-2列; 2-表示2及以后列 -2表示2及以前列)
-d 分隔符，按照指定分隔符分割列
```

#### demo

- 切割cut.txt第一列

```bash
[root@cluster5 shelltest]# more test.txt 
dong shen
guan zhen
wo  wo
lai  lai
le  le
##切割cut.txt第一列
[root@cluster5 shelltest]# cut -f 1 -d " " test.txt 
dong
guan
wo
lai
le
```

- ipconfig中 切割IPeth2   

```bash

Link encap:Ethernet  HWaddr 00:0C:29:8B:BF:14  
          inet addr:192.168.8.205  Bcast:192.168.8.255  Mask:255.255.255.0
          inet6 addr: fe80::20c:29ff:fe8b:bf14/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:13939 errors:0 dropped:0 overruns:0 frame:0
          TX packets:7718 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:1176367 (1.1 MiB)  TX bytes:910906 (889.5 KiB)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:16436  Metric:1
          RX packets:14 errors:0 dropped:0 overruns:0 frame:0
          TX packets:14 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:1034 (1.0 KiB)  TX bytes:1034 (1.0 KiB)
          
          
==================================================================
[root@cluster5 shelltest]# ifconfig eth2 |grep "inet addr"
          inet addr:192.168.8.205  Bcast:192.168.8.255  Mask:255.255.255.0
[root@cluster5 shelltest]# ifconfig eth2 |grep "inet addr" |cut -d ":" -f 2 
192.168.8.205  Bcast
[root@cluster5 shelltest]# ifconfig eth2 |grep "inet addr" |cut -d ":" -f 2 |cut -d " " -f 1 
192.168.8.205
```

### sed流编辑器

​		sed是一种流编辑器，它一次处理一行内容。处理时，把当前处理的行存储在临时缓冲区中，称为“模式空间”，接着用sed命令处理缓冲区中的内容，处理完成后，把缓冲区的内容送往屏幕。接着处理下一行，这样不断重复，直到文件末尾。文件内容并没有改变，除非你使用重定向存储输出

#### 语法

```tex
sed -e 操作 filename

操作类型
a       新增  'a newword' 在每行后都出现   '1a newword' 第2行出现
d       删除   '/d/word'  删除所有含word的行
s      查找并替换 's/word/neword/g'  /g 全部替换  不加只替换第一个
```

#### demo

- 指定行下添加一行  比如在a.txt第2行下添加 hello word 

  sed '2a  hello word' a.txt

- 删除a.txt文件所有包含wo的行

  sed '/wo/d' filename

- 将a.txt文件中wo替换为ni

  - 全部替换(g=global)：  sed 's/wo/ni/g' a.txt
  - 只替换第一个:              sed 's/wo/ni' a.txt

- 组合操作 ：

  - 在a.txt第2行下添加hello word 并且将文件中wo替换为ni

    sed -e '2a  hello word' -e 's/wo/ni/g' a.txt 