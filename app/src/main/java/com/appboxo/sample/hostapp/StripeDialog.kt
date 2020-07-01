package com.appboxo.sample.hostapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.CardInputWidget
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class StripeDialog(
    private val activity: Activity,
    private val price: Int,
    private val currency: String,
    private val name: String
) : BottomSheetDialog(activity, R.style.DialogStyle) {

    // 10.0.2.2 is the Android emulator's alias to localhost
    private val backendUrl = "http://10.0.2.2:4242/"
    private val httpClient = OkHttpClient()
    private lateinit var stripe: Stripe
    private val handler = Handler(Looper.getMainLooper())

    var doOnSuccess: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_stripe)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        startCheckout()
    }

    private fun startCheckout() {
        // For added security, our sample app gets the publishable key from the server
        val request = Request.Builder()
            .url(backendUrl + "stripe-key")
            .get()
            .build()
        httpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    displayAlert("Failed to load page", "Error: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        displayAlert(
                            "Failed to load page",
                            "Error: $response"
                        )
                    } else {
                        val responseData = response.body?.string()
                        val json = responseData?.let { JSONObject(it) } ?: JSONObject()
                        val publishableKey = json.getString("publishableKey")

                        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
                        stripe = Stripe(context.applicationContext, publishableKey)
                    }
                }
            })

        val payButton = findViewById<Button>(R.id.payButton)
        payButton?.setOnClickListener {
            pay()
        }
    }

    private fun pay() {
        // Collect card details on the client
        val cardInputWidget =
            findViewById<CardInputWidget>(R.id.cardInputWidget)!!
        cardInputWidget.paymentMethodCreateParams?.let { params ->
            stripe.createPaymentMethod(
                params,
                callback = object : ApiResultCallback<PaymentMethod> {
                    // Create PaymentMethod failed
                    override fun onError(e: Exception) {
                        displayAlert("Payment failed", "Error: $e")
                    }

                    override fun onSuccess(result: PaymentMethod) {
                        // Create a PaymentIntent on the server with a PaymentMethod
                        print("Created PaymentMethod")
                        pay(result.id, null)
                    }
                })
        }
    }

    // Create or confirm a PaymentIntent on the server
    private fun pay(paymentMethod: String?, paymentIntent: String?) {
        var json = ""
        if (!paymentMethod.isNullOrEmpty()) {
            json = """
                {
                    "useStripeSdk":true,
                    "paymentMethodId":"$paymentMethod",
                    "currency":"usd",
                    "items": [
                        {"id":"photo_subscription"}
                    ]
                }
                """
        } else if (!paymentIntent.isNullOrEmpty()) {
            json = """
                {
                    "paymentIntentId":"$paymentIntent"
                }
                """
        }
        // Create a PaymentIntent on the server
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(backendUrl + "pay")
            .post(body)
            .build()
        httpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    displayAlert("Payment failed", "Error: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    // Request failed
                    if (!response.isSuccessful) {
                        displayAlert("Payment failed", "Error: $response")
                    } else {
                        val responseData = response.body?.string()
                        val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()
                        val payError: String? = responseJson.optString("error")
                        val clientSecret: String? = responseJson.optString("clientSecret")
                        val requiresAction: Boolean = responseJson.optBoolean("requiresAction")
                        if (payError?.isNotEmpty() == true) {
                            // Payment failed
                            displayAlert(
                                "Payment failed",
                                "Error: $payError"
                            )
                        } else if (clientSecret?.isNotEmpty() == true && !requiresAction) {
                            // Payment succeeded
                            displayAlert(
                                "Payment succeeded",
                                "$clientSecret",
                                success = true
                            )
                        }
                        // Payment requires additional actions
                        else if (clientSecret?.isNotEmpty() == true && requiresAction) {
                            stripe.handleNextActionForPayment(activity, clientSecret)
                        }
                    }
                }
            })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Handle the result of stripe.authenticatePayment
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                when (paymentIntent.status) {
                    StripeIntent.Status.Succeeded -> {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        displayAlert(
                            "Payment succeeded",
                            gson.toJson(paymentIntent),
                            success = true
                        )
                    }
                    StripeIntent.Status.RequiresPaymentMethod -> {
                        // Payment failed – allow retrying using a different payment method
                        displayAlert(
                            "Payment failed",
                            paymentIntent.lastPaymentError!!.message ?: ""
                        )
                    }
                    StripeIntent.Status.RequiresConfirmation -> {
                        // After handling a required action on the client, the status of the PaymentIntent is
                        // requires_confirmation. You must send the PaymentIntent ID to your backend
                        // and confirm it to finalize the payment. This step enables your integration to
                        // synchronously fulfill the order on your backend and return the fulfillment result
                        // to your client.
                        print("Re-confirming PaymentIntent after handling a required action")
                        pay(null, paymentIntent.id)
                    }
                    else -> {
                        displayAlert(
                            "Payment status unknown",
                            "unhandled status: ${paymentIntent.status}",
                            success = true
                        )
                    }
                }
            }

            override fun onError(e: Exception) {
                // Payment request failed – allow retrying using the same payment method
                displayAlert("Payment failed", e.toString())
            }
        })
    }

    private fun displayAlert(
        title: String,
        message: String,
        success: Boolean = false
    ) {
        handler.post {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            if (success) {
                builder.setPositiveButton("Close") { _, _ ->
                    val cardInputWidget = findViewById<CardInputWidget>(R.id.cardInputWidget)
                    cardInputWidget?.clear()
                    doOnSuccess?.invoke()
                    dismiss()
                }
            } else {
                builder.setPositiveButton("Ok", null)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}