- 创建demo表

```sql
CREATE TABLE dept
(
    deptno int unsigned primary key auto_increment,
    dname varchar(20) not null default "",
    loc varchar(8) not null default ""
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE emp
(
    id int unsigned primary key auto_increment,
    empno mediumint unsigned not null default 0,
    ename varchar(20) not null default "",
    job varchar(9) not null default "",
    mgr mediumint unsigned not null default 0,
    hiredate date not null,
    sal decimal(7,2) not null,
    comm decimal(7,2) not null,
    deptno mediumint unsigned not null default 0
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```

- 存储函数需要设置相关参数
  
   This function has none of DETERMINISTIC…
  - 当二进制日志启用(log_bin)后，这个变量就会启用。它控制是否可以信任存储函数创建者，不会创建写入二进制日志引起不安全事件的存储函数。如果设置为0（默认值），用户不得创建或修改存储函数，除非它们具有除CREATE ROUTINE或ALTER ROUTINE特权之外的SUPER权限。 设置为0还强制使用DETERMINISTIC特性或READS SQL DATA或NO SQL特性声明函数的限制。 如果变量设置为1，MySQL不会对创建存储函数实施这些限制。 此变量也适用于触发器的创建
  - 因为二进制日志的一个重要功能是用于主从复制，而存储函数有可能导致主从的数据不一致。所以当开启二进制日志后，参数log_bin_trust_function_creators就会生效，限制存储函数的创建、修改、调用
  ```sql
  --查询参数
  show variables like 'log_bin_trust_function_creators'; 
  --设置参数
  set global log_bin_trust_function_creators=1;
  ---------------------------------------------------------
  --永久设置 vi my.cnf
  log_bin_trust_function_creators=1
  ```

- 编写函数
  ```sql
  ---随机产生字符串的函数
  delimiter $$ -- delimiter 命令 修改结束符为 $$
  create function rand_string(n int) returns varchar(255)
  begin
      declare chars_str varchar(100) default 'abcdefghijklmnopqrstuvwxyz';   --declare声明
      declare return_str varchar(255) default '';
      declare i int default 0;
      while i < n do
          set return_str = concat(return_str,substring(chars_str,floor(1+rand()*52),1));
          set i=i+1;
      end while;
      return return_str;
  end $$
  ---随机产生部门编号的函数
  delimiter $$
  create function rand_num() returns int(5)
  begin
      declare i int default 0;
      set i=floor(100+rand()*10);
      return i;
  end $$
  ```

- 创建存储过程
  ```sql
  delimiter $$
  create procedure insert_emp(in start int(10),in max_num int(10))
  begin
      declare i int default 0;
      set autocommit = 0;
      repeat
          set i = i+1;
          insert into emp(empno,ename,job,mgr,hiredate,sal,comm,deptno) values((start+i),rand_string(6),'salesman',0001,curdate(),2000,400,rand_num());
          until i=max_num
          end repeat;
      commit;
  end $$
  
  
  delimiter $$
  create procedure insert_dept(in start int(10),in max_num int(10))
  begin
      declare i int default 0;
      set autocommit = 0;
      repeat
          set i = i+1;
          insert into dept(deptno,dname,loc) values((start+i),rand_string(10),rand_string(8));
          until i=max_num
          end repeat;
      commit;
  end $$
  ```

- 调用存储过程
  ```sql
  DELIMITER ;
  CALL insert_dept(100, 10);
  DELIMITER ;
  CALL insert_emp(100001, 500000);
  ```
