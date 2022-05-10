# 1.用户创建

## CDB创建用户需要： C##username

```sql
create user C##username identified by userpassword;
```

## PDB中创建用户 需要提前切换到PDB容器库

```sql
alter session set container=ORCLPDB;
```

# 2. 打开容器库

容器库未打开会导致容器库用户无法登陆。

sqlplus / as sysdba

查看PDB数据状态

```sql
select con_id,name,open_mode from v$pdbs;
alter pluggable database ORCLPDB open;
alter session set container=ORCLPDB;
commit;
```

# 3 常用查询

```sql
1. 查询所有表
select * from user_tables;

```
