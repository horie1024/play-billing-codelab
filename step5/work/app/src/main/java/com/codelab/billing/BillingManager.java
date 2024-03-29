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
import com.android.billingclient.api.PurchasesUpdatedListener;
import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails.SkuDetailsResult;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * BillingManager that handles all the interactions with Play Store
 * (via Billing library), maintain connection to it through BillingClient and cache
 * temporary states/data if needed.
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";

    private final BillingClient mBillingClient;
    private final Activity mActivity;

    // Defining SKU constants from Google Play Developer Console
    private static final HashMap<String, List<String>> SKUS;
    static
    {
        SKUS = new HashMap<>();
        SKUS.put(SkuType.INAPP, Arrays.asList("gas", "premium"));
        SKUS.put(SkuType.SUBS, Arrays.asList("gold_monthly", "gold_yearly"));
    }

    private static final String SUBS_SKUS[] = {"gold_monthly", "gold_yearly"};

    public BillingManager(Activity activity) {
        mActivity = activity;
        mBillingClient = new BillingClient.Builder(mActivity).setListener(this).build();
        startServiceConnection(null);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        Log.i(TAG, "onPurchasesUpdated() response: " + responseCode);
    }

    /**
     * Trying to restart service connection.
     * <p>Note: It's just a primitive example - it's up to you to develop a real retry-policy.</p>
     * @param executeOnSuccess This runnable will be executed once the connection to the Billing
     *                         service is restored.
     */
    private void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponse) {
                Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
            final List<String> skuList, final SkuDetailsResponseListener listener) {
        mBillingClient.querySkuDetailsAsync(itemType, skuList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(SkuDetailsResult result) {
                        listener.onSkuDetailsResponse(result);
                    }
                });
    }

    public List<String> getSkus(@SkuType String type) {
        return SKUS.get(type);
    }

    public void startPurchaseFlow(String skuId, String billingType) {
        // TODO: Implement launch billing flow here
    }
}
