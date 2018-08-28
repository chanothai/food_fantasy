package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors
import kotlin.coroutines.experimental.CoroutineContext

interface Defferred<out T>: ProductRepo {
    suspend fun await(): T
}

data class ProductPaging(
        var products: MutableList<Product>
)

interface ProductRepo {
    fun create(product: Product?)
    fun remove(id: String)
    fun getAllWithPaging(limit: Int): ProductPaging?
    fun get(id: String): Product?
}

class ProductFireStore: ProductRepo {
    private val colProduct: String = "Products"
    private val db = FirebaseFirestore.getInstance()

    override fun create(product: Product?) {
        try {
            val docRef = db.collection(colProduct).document(product!!.id!!)
            Tasks.await(docRef.set(product))

        }catch (e: Exception) {
            throw Errors.UnableCreateProduct

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError

        }
    }

    override fun remove(id: String) {
        try {
            val document = queryProduct(id)
            val product:Product = document.toObjects(Product::class.java)[0]
            val docRemove = db.collection(colProduct).document(product.id!!)

            Tasks.await(docRemove.delete())
        }catch (e: Exception) {
            throw Errors.ProductNotFound

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError

        }
    }

    override fun getAllWithPaging(limit: Int): ProductPaging? {
        try {
            val documents = queryProduct(limit = limit)

            val products = documents.toObjects(Product::class.java)

            return ProductPaging(
                    products
            )
        }catch (e:Exception) {
            throw Errors.ProductNotFound
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun get(id: String): Product? {
        try {
            val document = queryProduct(id)
            return document.toObjects(Product::class.java)[0]

        }catch (e: Exception) {
            throw Errors.ProductNotFound

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError

        }
    }

    private fun queryProduct(id: String? = null, limit: Int = 1): QuerySnapshot {
        val docRef = db.collection(colProduct)

        val query: Query?

        if (id == null) {
            query = docRef.orderBy("updateDate").limit(limit.toLong())

            return Tasks.await(query.get())
        }

        query = docRef.whereEqualTo("id", id).limit(limit.toLong())
        return Tasks.await(query.get())
    }
}