[33mcommit ecf19d7e599ec63137246a435d18baad81978c5f[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Thu Oct 1 19:23:50 2015 +0100

    Switch to jndn 0.8 to fix ByteBuffer compare issue; for now, this requires a 'git pull; mvn install' for a locally-cloned jndn repo

[33mcommit 6f6e9b1d579c89def0ff5a66bc31918e7303656a[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Mon Sep 28 19:35:51 2015 +0100

    Re-format pom.xml

[33mcommit 4569a929fece880926bb6ea07547e9002228290e[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Mon Sep 28 19:24:59 2015 +0100

    Remove unnecessary test

[33mcommit 69e01051518a7c3b6a1bdfcf9fd31540df54ea94[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 23 17:59:55 2015 +0100

    Add license headers project-wide

[33mcommit 6e6bede2e724424230f42207674ebd0a976af09d[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 23 17:58:28 2015 +0100

    Apply code formatting tool to code files

[33mcommit 4d7bd44013b8f5060485b60cb5db8010c1f209f1[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 23 17:57:43 2015 +0100

    Replace @author references with Haitao's full name and e-mail

[33mcommit 5dc06f0f24e82991303740149f66363481bd8e9f[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Fri Sep 11 14:40:34 2015 -0700

    Change the setting of log levels

[33mcommit ad587924d9dfcc1d44104446859024285020da9a[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Fri Sep 11 14:21:11 2015 -0700

    Change the display format of FaceUri.

[33mcommit a6add27dc5511ee00c81e227ab2e165d77b74d80[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Thu Sep 10 17:57:17 2015 -0700

    Pass the connection with c++ NFD.

[33mcommit 66b170ba2147d100e58cb5b7ba1f44183758ae76[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Thu Sep 10 16:06:03 2015 -0700

    Add the function of deleting Face.

[33mcommit 7d4f7a7d8e1eb9c7dbb36e8ea0731462ed02eef5[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Thu Sep 10 14:31:21 2015 -0700

    Fix the AsynchronizedSocketChannel read and write bugs in multiple threads case.

[33mcommit aea95c74e3962e5e462a84c68b30569c40df7f3d[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Wed Sep 9 18:30:20 2015 -0700

    Fix the AsynchronizedSocketChannel read and write bugs in single thread case. Need to implement some mechanisms like the TCP window.

[33mcommit ebfb1a50082ca69d16ae2349f6250473640fd6db[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 9 03:07:58 2015 +0100

    Locate byte conversion bug in Name.Component compare

[33mcommit ee50ca82279fd66db11f85ebe36dfffe793dcdc4[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 9 01:00:15 2015 +0100

    Add non-working TcpFace test

[33mcommit 511610214f304bb03b3dc72060b95a2819d3d8fc[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Tue Sep 8 09:13:44 2015 -0700

    Bug fix.

[33mcommit fc7bada2cf0dbb3110cf199f022a3cb072effa49[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Fri Sep 4 18:58:02 2015 -0700

    Pass the test for one repeating Interest. Need more test.

[33mcommit b8474cda042cdd8dfcb698737bac1ed3f100f472[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Fri Sep 4 16:22:33 2015 -0700

    register prefix works for java app.

[33mcommit 9e796579635c0a643a37aeae59965ee0ed78739b[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Fri Sep 4 10:27:12 2015 -0700

    add onFaceAdd callback.

[33mcommit a0e5988097859619605bedba39537cedeabb18ee[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Sep 4 17:49:27 2015 +0100

    Add manual prefix registration command; TODO in the future, this type of command should be registered as an internal face on the forwarder but the current protocol/face interface does not make this possible

[33mcommit 4502c3069ce90d79226fadf55d43f5ce6eb7963a[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Thu Sep 3 14:21:49 2015 -0700

    1. fix the bug of PitEntry. 2. implement the callbacks of OnDataReceived and OnInterestReceived.

[33mcommit beb1eb34ac3c207fd6e539c09312e7e5055888d1[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Thu Sep 3 21:25:21 2015 +0100

    Add clarifying documentation for PitEntry

[33mcommit bad008329936a90389694b904c261ef8ef181298[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Wed Sep 2 17:54:02 2015 -0700

    Fix the problem of  ipv4 and ipv6 can bind on a same port. Change the callback systems.

[33mcommit d3672ace390498fa205aff7e5d3e636230ff86d4[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 2 20:04:49 2015 +0100

    Fix SortedSetCs eviction using simple FIFO queue

[33mcommit 5a409eaa37dc6c007864c4cb855419e0a4432f49[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 2 19:34:03 2015 +0100

    Add HashMapRepo tests

[33mcommit 8ae3916547f50b80311b0a0c52154541ece28e0c[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Sep 2 19:06:39 2015 +0100

    Add PIT test
    
    I fixed a couple of bugs but I suspect they fail because the logic in Pit.java
    involves searching/adding/removing lists of PitEntries instead of just a
    PitEntry. I suspect that PitEntry already wraps a list of Interests so I don't
    see a need for an additional list level (list of entries + list of interests)

[33mcommit 95ad18deda75bc7cca53f8cf1e373adf8664379d[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Tue Sep 1 13:04:30 2015 -0700

    Fix some bugs when try to run the consumer.

[33mcommit c4158dfb8a40f00d51b39c3645c37d89a0e9caab[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Mon Aug 31 18:52:32 2015 -0700

    Revise Forwarder.

[33mcommit f8ecc5fdd38770d79d7421af6c51b8c98ff79e4d[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Sun Aug 30 18:36:51 2015 -0700

    Implement "FaceManager". Next, test the function of the forwarder.

[33mcommit c6dd0f5f9c5461aa0c60364f3402532c76e669e8[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Thu Aug 27 22:08:51 2015 +0100

    Add ContentStore test; still fails due to datas not being replaced

[33mcommit 5d9c613c9effd1221b6146270ae868aaf7db0e47[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Wed Aug 26 18:11:20 2015 -0700

    Add data pipelines for ForwardingPipeline.

[33mcommit 98a0c5e55972e6e4130c9253b7decf4b2336177c[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Thu Aug 27 01:01:23 2015 +0100

    Rename internal forwarding mechanism

[33mcommit f38adf6110871c12b5d5dad16bfc00c375780eff[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Thu Aug 27 00:56:56 2015 +0100

    Manual merge of Haitao's changes; compiles, needs tests

[33mcommit 89101beb3350aa512c38c3cb062d1a6fabc718c0[m
Merge: 3117b0a 7cce7f5
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Aug 26 16:29:24 2015 -0700

    Merge remote-tracking branch 'origin/forwarder-wip'
    
    Conflicts:
    	src/com/intel/jndn/forwarder/api/Strategy.java
    	src/com/intel/jnfd/deamon/fw/FaceTable.java
    	src/com/intel/jnfd/deamon/fw/Trigger.java
    	src/com/intel/jnfd/deamon/table/cs/Cs.java
    	src/com/intel/jnfd/deamon/table/cs/SortedSetCs.java
    	src/com/intel/jnfd/deamon/table/pit/PitEntry.java
    	src/com/intel/jnfd/deamon/table/pit/PitFaceRecord.java
    	src/com/intel/jnfd/deamon/table/strategy/StrategyChoice.java

[33mcommit 7cce7f5fd19756eb4930b6519f80f8efc758844f[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Wed Aug 26 15:49:35 2015 -0700

    Add "fowarder".

[33mcommit 3117b0a307de0730e3090174f379d0db0ef8d93e[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Aug 26 21:15:05 2015 +0100

    Add callback refactoring

[33mcommit 938b50951227819bf17eebf83f773ffb48336cf1[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 15:13:49 2015 -0700

    Add determineOutgoingFaces() to strategies

[33mcommit cf54fd3149fe47d20145984d1f926ec428bcefaa[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 14:42:32 2015 -0700

    WIP add initial data/interest callbacks

[33mcommit 2876ae1202e42973358f290c4800140db162d8c5[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 14:26:57 2015 -0700

    Remove OnFaceDestroyed

[33mcommit 5091fcd5fa421046b920dcb815b4ffa53c3f205c[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 14:20:01 2015 -0700

    WIP continue re-factor

[33mcommit 29387b34f9e8c6369b6dfe7f418dd2ccd750f7a4[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 09:51:58 2015 -0700

    Fix TestFaceUri assert

[33mcommit dca95268c454a044150dd057f6ab07be23875639[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 09:48:23 2015 -0700

    WIP additional re-factor tweaks

[33mcommit ac92552fbcbb0f416728526bceda5f3c68b3aa4c[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Fri Aug 21 09:41:47 2015 -0700

    WIP high-level interface re-factor

[33mcommit b07e84e48da0e08a294af39716b6a1f7d3066784[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Wed Aug 19 06:41:14 2015 -0700

    Add "facetable".

[33mcommit 1cb0b22ae63b1effb1858717989cdbd4f1cf4bfd[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Tue Aug 18 23:10:03 2015 -0700

    Add measurement table

[33mcommit e621099b0c982fe751e40efd61a630181cd1308f[m
Author: andrewsbrown <andrew@casabrown.com>
Date:   Wed Aug 19 17:41:23 2015 +0100

    Add license information

[33mcommit 79eaa9402c600b3c08904a46b921293330e8e814[m
Author: Andrew Brown <andrew.brown@intel.com>
Date:   Wed Aug 19 17:31:43 2015 +0100

    Add README

[33mcommit bba0f5ebeb9c6323391dc2e42437144b69dd1fee[m
Author: Andrew Brown <andrew.brown@intel.com>
Date:   Wed Aug 19 17:25:27 2015 +0100

    Ignore build artifacts

[33mcommit fd08162339884ebbab3cde7b7832b6ac280e4b91[m
Author: Andrew Brown <andrew.brown@intel.com>
Date:   Wed Aug 19 17:24:14 2015 +0100

    Remove build artifacts

[33mcommit ddad86ea47a099085260ba958c2ca6c12e6a9e5d[m
Author: zhtaoxiang <zhtaoxiang@gmail.com>
Date:   Tue Aug 18 17:11:42 2015 -0700

    the tcp face and tables except for mesurement table
