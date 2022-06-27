/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilekosmos.android.clubs.ui.model

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "clubsList")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, clubsList: Any?) {
    view.visibility = if (clubsList != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError2")
fun hideIfNetworkError2(view: View, isNetWorkError: Boolean) {
    view.visibility = View.GONE

    if (isNetWorkError) {
        view.visibility = View.VISIBLE
    }
}