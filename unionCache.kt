


class DnList(val host:String) {}


class UnionCache private constructor() {

    companion object {
        private val core by lazy {
            UnionCache()
        }

         fun instance():UnionCache {
            return core
        }
    }

    fun <T> cache(clazz:Class<T>):CacheTask<T> {
       return CacheTask()
    }


}

interface CTask<T> {
    fun filter(filterTask:(T)->Boolean):CTask<T>
}


class CacheTask<T>:CTask<T> {

    override fun filter(filterTask: (T) -> Boolean):CacheTask<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun database(sqlTask:()->List<T>):DBTask<T> {
        val list = sqlTask()
        return DBTask()
    }
}

class DBTask<T> {

    fun request(netTask:()->List<T>):NetTask<T> {
        return NetTask()
    }

    fun expire(expireCondition:()->Boolean):DBTask<T> {
        return this
    }
}

class NetTask<T>{

    fun sync():List<T> {
        return listOf()
    }

    fun aync(callback:(List<T>) -> Unit) {
        //return to a async task to wait for the result

    }
}


fun main(args:Array<String>) {

    val data = UnionCache.instance()
        .cache(DnList::class.java)
        .filter {
            it.host.contains("http:www.baidu.com")
        }
        .database {
            listOf()//data from db
        }.expire {
            true
        }.request {
            listOf()
        }.sync()

}
