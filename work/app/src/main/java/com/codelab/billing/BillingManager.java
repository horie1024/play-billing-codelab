/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codelab.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Implement BillingManager that will handle all the interactions with Play Store
 * (via Billing library), maintain connection to it through BillingClient and cache
 * temporary states/data if needed.
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";
    private final BillingClient mBillingClient;
    private Activity mActivity;

    public BillingManager(Activity activity) {

        mActivity = activity;

        mBillingClient = new BillingClient.Builder(activity).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int resultCode) {
                if (resultCode == BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + resultCode);
                } else {
                    Log.w(TAG, "onBillingSetupFinished() error code: " + resultCode);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    public void startPurchaseFlow(String skuId, String billingType) {
        BillingFlowParams billingFlowParams = new BillingFlowParams.Builder()
                .setType(billingType)
                .setSku(skuId)
                .build();
        mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated() response: " + responseCode);
        if (responseCode == BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                //handlePurchase(purchase);
                Log.d(TAG, "onPurchasesUpdated() purchase sku: " + purchase.getSku());
            }
        } else if (responseCode == BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, "onPurchasesUpdated() response: " + responseCode + "Handle an error caused by a user cancelling the purchase flow.");
        } else {
            // Handle any other error codes.
            Log.d(TAG, "onPurchasesUpdated() response: " + responseCode + " Handle any other error codes.");
        }
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String skuType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        mBillingClient.querySkuDetailsAsync(skuType, skuList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(SkuDetails.SkuDetailsResult result) {
                        listener.onSkuDetailsResponse(result);
                    }
                });
    }

    private static final HashMap<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("gas", "premium"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("gold_monthly", "gold_yearly"));
    }

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }
}
