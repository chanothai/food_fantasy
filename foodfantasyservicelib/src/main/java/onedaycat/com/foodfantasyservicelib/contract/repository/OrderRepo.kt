package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.error.Errors
import java.util.*
import kotlin.collections.HashMap

interface OrderRepo {
    fun upsert(order: Order)
    fun get(id: String): Order
}

class OrderFireStore: OrderRepo {
    private val colOrder: String = "Orders"
    private val db = FirebaseFirestore.getInstance()

    override fun upsert(order: Order) {
        try {
            val docRef = db.collection(colOrder).document(order.id!!)

            val arrPQTY = mutableListOf<HashMap<String, Any>>()

            for (product in order.products) {
                val docPQTY = HashMap<String, Any>()
                docPQTY["productId"] = product!!.productId!!
                docPQTY["price"] = product.price!!
                docPQTY["qty"] = product.qty

                arrPQTY.add(docPQTY)
            }

            val docData = HashMap<String, Any>()
            docData["id"] = order.id!!
            docData["userId"] = order.userId!!
            docData["products"] = arrPQTY
            docData["totalPrice"] = order.totalPrice
            docData["createDate"] = order.createDate!!
            docData["status"] = order.status.toString()


            Tasks.await(docRef.set(docData))
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun get(id: String): Order {
        try {
            val docRef = db.collection(colOrder).document(id)

            val docOrder = Tasks.await(docRef.get()) ?: throw Errors.NotOrderOwner
            val products = docOrder.get("products") as MutableList<HashMap<String, Any>>

            val arrPQTY = mutableListOf<ProductQTY?>()
            for (product in products) {
                val price = product["price"] as Long
                val qty = product["qty"] as Long

                val pQty = ProductQTY(
                        productId = product["productId"] as String,
                        price = price.toInt(),
                        qty = qty.toInt()
                )

                arrPQTY.add(pQty)
            }

            val status = docOrder.getString("status")
            val orderStatus:State.OrderStatus = State.OrderStatus.valueOf(status!!)

            return Order(
                    docOrder.getString("id"),
                    docOrder.getString("userId"),
                    arrPQTY,
                    docOrder.getLong("totalPrice")!!.toInt(),
                    docOrder.getString("createDate"),
                    orderStatus
            )

        }catch (e:Exception) {
            throw Errors.UnableGetOrder
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

}