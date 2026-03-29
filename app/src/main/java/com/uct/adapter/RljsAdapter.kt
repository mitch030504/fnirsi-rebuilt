package com.uct.adapter

import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.officialwebsite.R
import com.uct.entity.RljsEntity
import com.uct.utlis.Global

class RljsAdapter : BaseQuickAdapter<RljsEntity, BaseViewHolder>(R.layout.layout_rljs) {
    override fun convert(holder: BaseViewHolder, item: RljsEntity) {
        val energy = item.nltj / SCALE
        val resistance = item.rltj / SCALE
        val compensated = if (item.dcdy == 0.0) {
            0.0
        } else {
            (resistance / item.dcdy) * item.xl
        }
        Log.e("RljsAdapter", "convert: $energy   ${item.dcdy}   ${item.xl}")
        holder.setText(R.id.tv_rljs2, Global.get5Num(energy.toString()))
            .setText(R.id.tv_rljs3, Global.get5Num(resistance.toString()))
            .setText(R.id.tv_rljs4, item.dcdy.toString())
            .setText(R.id.tv_rljs5, (item.xl * 100.0).toString())
            .setText(R.id.tv_rljs6, Global.get5Num(compensated.toString()))
            .setText(R.id.tv_rljs1, item.groupNum.toString())
    }

    private companion object {
        const val SCALE = 100000.0
    }
}
