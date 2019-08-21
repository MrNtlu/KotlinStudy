package com.mrntlu.kotlinstudy.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.mrntlu.kotlinstudy.R
import com.mrntlu.kotlinstudy.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter=DogsListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)

        viewModel=ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=dogsListAdapter
        }

        refreshLayout.setOnRefreshListener {
            dogsList.visibility=View.GONE
            listError.visibility=View.GONE
            loadingView.visibility=View.VISIBLE
            viewModel.refreshBypassCache()
            refreshLayout.isRefreshing=false
        }

        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.dogs.observe(viewLifecycleOwner, Observer {
            it?.let {
                dogsList.visibility=View.VISIBLE
                dogsListAdapter.updateDogList(it)
            }
        })

        viewModel.dogsLoadError.observe(viewLifecycleOwner, Observer {
            it?.let {
                listError.visibility=if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            it?.let{
                loadingView.visibility=if (it) View.VISIBLE else View.GONE
                if (it){
                    listError.visibility=View.GONE
                    dogsList.visibility=View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.actionSettings == item.itemId){
            navController.navigate(R.id.actionSettings)
        }
        return super.onOptionsItemSelected(item)
    }
}
