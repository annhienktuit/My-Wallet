package com.annhienktuit.mywallet.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.Limitation
import com.google.android.material.textfield.TextInputEditText

class LimitationAdapter(val context: Context, val LimitationList: ArrayList<Limitation>) : RecyclerView.Adapter<LimitationAdapter.LimitationViewHolder>() {
    inner class LimitationViewHolder(val v: View): RecyclerView.ViewHolder(v){
        val title: TextView = v.findViewById(R.id.tvTitle)
        //val subtitle: TextView = v.findViewById(R.id.tvSubTitle)
        val target: TextView = v.findViewById(R.id.tvTarget)
        //val ivGroup: ImageView = v.findViewById(R.id.ivGroup)
        val mMenus: ImageView = v.findViewById(R.id.ivMore)

        init{
            mMenus.setOnClickListener {
                popupMenus(it)
            }
        }

        private fun popupMenus(v:View) {
            val position = LimitationList[adapterPosition]
            val popupMenus = PopupMenu(context,v)
            popupMenus.inflate(R.menu.show_menu_limitation)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val v = LayoutInflater.from(context).inflate(R.layout.dialog_add_limitation,null)
                        val group = v.findViewById<AutoCompleteTextView>(R.id.textCategoryLimit)
                        val target = v.findViewById<TextInputEditText>(R.id.tfTarget)
                        AlertDialog.Builder(context)
                            .setView(v)
                            .setPositiveButton("Add"){
                                    dialog,_->
                                position.limitedGroup = group.text.toString()
                                position.target = target.text.toString()
                                notifyDataSetChanged()
                                Toast.makeText(context,"Limitation Information is Edited", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.delete->{
                        /**set delete*/
                        AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("Are you sure delete this Information")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                LimitationList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(context,"Deleted this Information", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_limitation_item, parent, false)
        return LimitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LimitationViewHolder, position: Int) {
        val newList = LimitationList[position]
        holder.title.text = newList.limitedGroup
        //holder.subtitle.text = newList.moneyLeft.toString()
        holder.target.text = newList.target.toString()
    }

    override fun getItemCount(): Int {
        return LimitationList.size
    }
}