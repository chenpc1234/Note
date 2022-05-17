# ElasticSearch

## Docker安装ElasticSearch

1. 下载ealastic search和kibana

   ```bash
   docker pull elasticsearch:7.6.2
   docker pull kibana:7.6.2
   ```

2. 配置

   ```bash
   mkdir -p /mydata/elasticsearch/config
   mkdir -p /mydata/elasticsearch/data
   echo "http.host: 0.0.0.0" >/mydata/elasticsearch/config/elasticsearch.yml
   chmod -R 777 /mydata/elasticsearch/
   ```

3. 启动Elastic search

   ```dockerfile
   docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
   -e  "discovery.type=single-node" \
   -e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
   -v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
   -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
   -v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
   -d elasticsearch:7.6.2 
   #设置开机启动elasticsearch
   docker update elasticsearch --restart=always
   ```

4. 启动kibana

   ```dockerfile
   docker run --name kibana -e ELASTICSEARCH_HOSTS=http://192.168.121.150:9200 -p 5601:5601 -d kibana:7.6.2
   #设置开机启动kibana
   docker update kibana  --restart=always
   ```

5. 测试

   1. 查看elasticsearch版本信息： http://192.168.121.150:9200/
   2. 显示elasticsearch 节点信息： http://192.168.121.150:9200/_cat/nodes
   3. 访问Kibana： http://192.168.121.150:5601/app/kibana

## 索引/查询/删除一个文档

PUT customer/external/1

GET /customer/external/1

DELETE customer/external/1

DELETE customer

## Query DSL

### 基本语法

- query 
  - match_all 查询类型【代表查询所有的所有】，es中可以在query中组合非常多的查询类型完成复杂查询；

- from+size

  - 限定，完成分页功能；

- sort

  - 排序多字段排序，会在前序字段相等时后续字段内部排序，否则以前序为准；

- "_source": 

  - 指定查询的字段，不添加则查询所有字段

  ```json
  GET bank/_search
  {
    "query": {
      "match_all": {}
    },
    "from": 0,
    "size": 5,
    "sort": [
      {
        "account_number": {
          "order": "desc"
        }
      }
    ],
    "_source": ["balance","firstname"]
  
  }
  ```

### match 【匹配查询】

match匹配查询 基本类型（非字符串），精确控制   match返回account_number=20 的数据。

```json
GET bank/_search
{
  "query": {
    "match": {
      "account_number": "20"
    }
  }
}
```

字符串，全文检索，最终会按照评分进行排序，会对检索条件进行分词匹配。  "address.keyword"  会指定完全匹配 才可以

```json
GET bank/_search
{
  "query": {
    "match": {
      "address": "kings"
    }
  }
}
```

### match_phrase 【短句匹配】

将需要匹配的值当成一整个单词（不分词）进行检索

```json
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address": "mill road"
    }
  }
}
#查出address中包含mill_road的所有记录，并给出相关性得分
#如果用match，那么 "mill road"会分开成为2个单词
```



### multi_math【多字段匹配】

state或者address中包含mill，并且在查询过程中，会对于查询条件进行分词。

```json
GET bank/_search
{
  "query": {
    "multi_match": {
      "query": "mill",
      "fields": [
        "state",
        "address"
      ]
    }
  }
}
```

### bool【复合查询】

复合语句可以合并，任何其他查询语句，包括符合语句。这也就意味着，复合语句之间可以互相嵌套，可以表达非常复杂的逻辑。

- must：必须达到must所列举的所有条件
- must_not，必须不匹配must_not所列举的所有条件。
- should，应该满足should所列举的条件。
  - should：应该达到should列举的条件，如果到达会增加相关文档的评分，并不会改变查询的结果。
  - 如果query中只有should且只有一种匹配规则，那么should的条件就会被作为默认匹配条件而去改变查询结果。

实例：查询gender=m，并且address=mill的数据，但是age不等于38的

```json
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "gender": "M"
          }
        },
        {
          "match": {
            "address": "mill"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "age": "38"
          }
        }
      ]
    }
  }
```



```json
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "gender": "M"
          }
        },
        {
          "match": {
            "address": "mill"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "age": "18"
          }
        }
      ],
      "should": [
        {
          "match": {
            "lastname": "Wallace"
          }
        }
      ]
    }
  }
}
```

### Filter【结果过滤】

这里先是查询所有匹配address=mill的文档，然后再根据10000<=balance<=20000进行过滤查询结果，filter在使用过程中，并不会计算相关性得分：

```json
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        }
      ],
      "filter": {
        "range": {
          "balance": {
            "gte": "10000",
            "lte": "20000"
          }
        }
      }
    }
  }
}
```

### Aggregation【执行聚合】

语句结构如下：

```json
"aggs":{
    "aggs_name"{
        "AGG_TYPE":{
    		...
		}
     }
}
# aggs_name  :聚合的名字，方便展示在结果集中
# AGG_TYPE   : 聚合的类型(avg,term,terms)
```


按照年龄聚合，并且求这些年龄段的这些人的平均薪资

```json
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "ageAgg": {
      "terms": {
        "field": "age",
        "size": 100
      },
      "aggs": {
        "ageAvg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 0
}
```





