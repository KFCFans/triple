# triple
A high-performance, full-duplex, java based open source RPC framework（全双工RPC框架）



### 简介

[Apache Dubbo](https://dubbo.apache.org/zh-cn/)是一款极度优秀的高性能RPC框架，能满足绝大多数微服务场景的需求。然而，和市面上现有的开源成熟的各种RPC框架一样，dubbo只支持单向调用（即Consumer -> Provider），不允许双向互调（当然不支持这个需求也是合理的，微服务架构讲究的就是服务拆分和解耦）。然而，总是有一些区域自治的小系统希望有这种低成本的双向通讯的能力，这也就是本框架诞生的初衷，也是本框架名称的来源（如下图所示）。

dubbo(double)  ----> Quadra(单向变全双工，X2) ---->triple（开发者与阿里巴巴工程师之间的水平差距，-1）