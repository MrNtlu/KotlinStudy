package com.mrntlu.kotlinstudy.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.mrntlu.kotlinstudy.R
import com.mrntlu.kotlinstudy.databinding.FragmentDetailBinding
import com.mrntlu.kotlinstudy.model.DogPalette
import com.mrntlu.kotlinstudy.util.getProgressDrawable
import com.mrntlu.kotlinstudy.util.loadImage
import com.mrntlu.kotlinstudy.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*

class DetailFragment : Fragment() {

    private lateinit var navController: NavController
    private var dogUUID=0
    private lateinit var viewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        return dataBinding.root
        //return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)

        arguments?.let {
            dogUUID=it.getInt("dogUUID")
            println(dogUUID)
        }

        viewModel=ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUUID)

        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer {
            it?.let{
                dataBinding.dog=it

                it.imageUrl?.let{
                    setupBackgroundColor(it)
                }
                /*dogName.text=it.dogBreed
                dogPurpose.text=it.bredFor
                dogTemperament.text=it.temperament
                dogLifespan.text=it.lifeSpan
                dogImage.loadImage(it.imageUrl, getProgressDrawable(dogImage.context))*/
            }
        })
    }

    private fun setupBackgroundColor(url:String){
        Glide.with(this).asBitmap().load(url).into(object: CustomTarget<Bitmap>(){
            override fun onLoadCleared(placeholder: Drawable?) {}

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Palette.from(resource).generate{
                    val intColor=it?.lightMutedSwatch?.rgb ?: 0
                    val myPalette=DogPalette(intColor)
                    dataBinding.palette=myPalette
                }
            }
        })
    }
}
