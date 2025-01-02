package com.eazywrite.app.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.recyclerview.widget.RecyclerView
import com.eazywrite.app.ui.chart.ChartPage
import com.eazywrite.app.ui.gpt.ChatPage
import com.eazywrite.app.ui.home.HomePage
import com.eazywrite.app.ui.profile.ProfilePage
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.applyContent
import com.eazywrite.app.util.fillMaxSize


class ViewPage2ViewAdapter(
    private val parentView: View,
    private val localScaffoldPaddingValues: PaddingValues
) : RecyclerView.Adapter<ViewPage2ViewAdapter.ViewPage2ViewHolder>() {

    class ViewPage2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPage2ViewHolder {
        val context = parent.context
        val view = ComposeView(context).applyContent {
            EazyWriteTheme {
                CompositionLocalProvider(LocalView provides parentView) {
                    CompositionLocalProvider(LocalMainScreenScaffoldPaddingValues provides localScaffoldPaddingValues) {
                        when (viewType) {
                            0 -> HomePage()
                            1 -> ChartPage()
                            2 -> ChatPage()
                            else -> ProfilePage()
                        }
                    }
                }
            }
        }
        return ViewPage2ViewHolder(view.fillMaxSize())
    }

    override fun getItemCount() = 4

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewPage2ViewHolder, position: Int) {}
}