<h1 align = "center">关于BigTable存储系统的个人理解</h1>
<h4 align = "center">18301055-徐子涵</h4>

### 综述

##### 		Bigtable是一个用来管理大规模结构数据的分布式系统，其设计初衷就是为了对存储在大量机器上的海量数据进行有效管理。作为Google的三驾马车之一，Bigtable具有适用性广、可扩展性强、高性能和可用性高等特点，并启发了后来诸如HBase等非关系数据库。

##### 		在这篇论文中，我将基于对Bigtable论文的阅读，讨论BigTable的数据存储模型，其具体实现方式，以及使用这种方式所带来的优缺点。

### 存储模型

​		Bigtable这个存储系统，最初的目的就是为了解决google在企业运营中碰到的实际问题：当有海量的数据存储（pb级）在分布式机群中时，如何对存储的信息进行有效的管理，并有效应对不同数据大小、不同响应时间要求的实际业务。针对这一点，Bigtable进行了如下设计：

​		1，Bigtable的数据通过一个稀疏的、分布式的、持久化存储的多维度排序Map来实现。这一Map以行键值，列键值，以及时间戳三个量进行索引。而存储的信息则以一个未经解析的字节数组来进行存储。

​		2，原子性。Bigtable仅支持单行事务操作（在论文发表时），而在单一行内，每一个读或写操作都是原子性的，不受列数限制。

​		3，分区。Bigtable会根据关键字的进行排序，并将结果进行分区，相同类型分区列族保持一致，大小在100MB~200MB左右。

​		以上设计主要是出于业务的需要，综合考虑了功能和性能两方面因素。Bigtable通过三位一体的复合型键值，实现了一个三维的数据存储。这种处理方式可以通过利用存储结构，明确的表达数据的次序、类型和时间，更好的对数据进行分类。使用半结构化的数据存储格式可以在实际的操作中提供更大的操作空间，通过序列化等手段对数据的“类型”进行扩充。

​		Bigtable将事务局限于单行，这一操作主要是为了提高系统性能，降低系统复杂程度。Bigtable的研发者在开发过程中发现，相较于跨行的复杂事物，用户更多时候只关心单行之内读写操作的原子性，而采用多行事物则有可能会引入大量的程序错误，如多行操作的时不同事务间的资源冲突。而对单行进行原子性要求，只需在指定行加锁即可解决问题。

​		通过分区，BigTable将一个海量的大数据分为无数的小数据，将原本pb级别的数据分化成mb级别的小块，以此提高程序的响应速度，同时便于对数据进行各种操作。而由于Bigtable会根据关键词进行排序，在优化数据显示的同时，故具有相似性质的数据将会有很高的概率产生聚集，便于后续的数据分析。

### 实现方式

​		BigTable的实现需要依靠一些其他的google基础框架，像是使用GFS进行日志和数据文件存储，SSTable格式进行数据存储，Chubby提供分布式锁。由于这里我们主要关注于BigTable如何使用tablet进行的区域划分，对于其他依赖我们就不再赘述。BigTable的设计目的就是用多机器处理大数据，在此目标下，将大数据切分成小块便是一个很自然的思想。为了实现这一划分方式，BigTabel需要依赖于三个组件：链接到每个客户程序的库、一个Master服务器和多个tablet服务器。

​		BigTable使用一个三层的B+树来进行tablet位置信息的存储。第一层是一个存储在Chubby中的文件，它包含了root tablet的位置信息。root tablet在一个特殊的元数据（METADATA）表包含了里所有的tablet的位置信息。每一个元数据tablet包含了一组用户tablet的的位置信息。root tablet实际上只是元数据表的第一个tablet，只不过对它的处理比较特殊—root tablet永远不会被分割—这就保证了tablet的位置层次不会超过三层。

​		tablet在划分好后，将会在那啥特然服务器的管理下被分配给不同的tablet服务器。每个tablet的的实际大小都是动态的，其具体状况需要依赖于master服务器的分配。Master服务器主要负责以下操作：为tablet服务器分配tablets，检测新加入的或者过期失效的table服务器、平衡tablet服务器的负载、以及对GFS中的文件进行垃圾收集，模式修改操作等等。为了避免master服务器的负载过重，在Bigtable中用户直接与tablet服务器进行读写操作，不需要与master服务器通信即可取得信息，以此来降低master的任务量。

​		master服务器主要负责对tablet的管理，而数据的实际操作则依赖tablet服务器进行处理。tablet的持久化信息存在GFS上，数据操作则记录到日志中。当数据需要从错误中恢复时，就会从这两者中提取对应信息。而对于读写操作，tablet服务器会在检查操作正确性和合法性之后予以执行，并且不受tablet分割合并的影响。

### 优缺点

​		通过以上分析可以看到，Bigtable面对多机器的海量存储数据，采用了一个分而化之的思想，将一个大数据打碎，把碎片分配给不同的服务器，从而使每个服务器处理较小体量的数据，由于Bigtable的有序性，相似的数据将会被大概率归类到相同的tablet中，从而是完成一个任务所需访问的tablet数下降。而在每个小模块的处理上，Bigtable又在功能和性能之间做了一定的选择，一次更好满足实际需要。

​		BigTable这种方式的优点有很多，在这里主要提出最为重要的几点。首先是线性优化性好。当一个节点出现故障，或者某个节点负载过高，master节点都会将其重新平衡至其他节点上，从而提高性能。其次是采用原子性，不支持事务，在不影响用户使用的前提下，减少了许多事务的锁判断，从而避免系统过于复杂，提升系统性能。】

​		BigTable这种方式的也带来了一些问题。缺乏对事务的支持，导致在实际应用中会进行事务的编写，可能反而会引入更多的问题。因此在后续版本中，Bigtable也加入了对事务的支持。其次，固定的存储结构导致其对小型数据集的效果不佳。三层B+树对于大规模的数据集有着很好的排序压缩功能，但是对于小规模的数据集则会产生很多的空间浪费。

#### 总结

​		BigTable作为一款2008年发表的数据存储系统，其同时兼具有优良的性能及对业务的可扩展性。BigTable本身并不具有很高的创新性，很多技术和设计理念都是来源于已经存在的东西。其优异表现源自于其设计者对于对需求的准确把握，和在功能之间的明确选择。除去BigTable技术特点，其系统设计理念也使我们很好的学习目标。

​		



​		