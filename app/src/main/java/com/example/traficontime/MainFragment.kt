package com.example.traficontime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_simple_text.view.*

class MainFragment : Fragment() {

    var onItemClickListener: ((Station) -> Unit)? = null

    private val enhancedRecyclerAdapter =
            object : EnhancedRecyclerAdapter<Station>(R.layout.item_simple_text) {
                override fun bindItem(parentView: View, item: Station) {
                    parentView.tv_title.text = item.name
                }
            }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_saved.adapter = enhancedRecyclerAdapter
        rv_saved.addItemDecoration(
                DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                )
        )

        enhancedRecyclerAdapter.onItemClickListener = {
            onItemClickListener?.invoke(it)
        }
    }

    fun setSavedList(list: List<Station>) {
        enhancedRecyclerAdapter.submitList(list)
    }

}