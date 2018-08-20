package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.error.Errors

interface CartRepo {
    fun upsert(cart: Cart?)
    fun getByUserID(userId: String): Cart?
}

class CartFireStore: CartRepo {
    private val colCart: String = "Carts"
    private val db = FirebaseFirestore.getInstance()

    override fun upsert(cart: Cart?) {
        try {
            val docRef = db.collection(colCart).document(cart!!.userId!!)

            Tasks.await(docRef.set(cart))
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getByUserID(userId: String): Cart? {
        try {
            val docRef = db.collection(colCart).document(userId)
            val document = Tasks.await(docRef.get())
            return document.toObject(Cart::class.java) ?: throw Errors.CartNotFound

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }
}