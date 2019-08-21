package com.mrntlu.kotlinstudy.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.mrntlu.kotlinstudy.R
import com.mrntlu.kotlinstudy.databinding.ItemDogBinding
import com.mrntlu.kotlinstudy.model.DogBreed
import com.mrntlu.kotlinstudy.util.getProgressDrawable
import com.mrntlu.kotlinstudy.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter (val dogsList:ArrayList<DogBreed>) : RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(),DogClickListener {

    fun updateDogList(newDogsList: List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        //val view=inflater.inflate(R.layout.item_dog,parent,false)
        val view=DataBindingUtil.inflate<ItemDogBinding>(inflater,R.layout.item_dog,parent,false)
        return DogViewHolder(view)
    }

    override fun getItemCount() = dogsList.size


    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog=dogsList.get(position) //for databinding
        holder.view.listener=this

        /*val dog=dogsList.get(position)
        holder.view.name.text=dog.dogBreed
        holder.view.lifeSpan.text=dog.lifeSpan

        holder.itemView.setOnClickListener {
            val bundle=bundleOf("dogUUID" to dog.uuid)
            Navigation.findNavController(it).navigate(R.id.actionDetailFragment, bundle)
        }
        holder.view.imageView.loadImage(dog.imageUrl, getProgressDrawable(holder.view.imageView.context))
        */
    }

    override fun onDogClicked(v: View) {
        val bundle=bundleOf("dogUUID" to v.dogId.text.toString().toInt())
        Navigation.findNavController(v).navigate(R.id.actionDetailFragment,bundle)
    }

    class DogViewHolder(var view:ItemDogBinding):RecyclerView.ViewHolder(view.root)
}