package com.example.gd8_d_0932

import com.example.gd8_d_0932.databinding.LayoutTooltipBinding
//import kotlinx.android.synthetic.main.layout_tooltip.view.*
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow


class CustomInfoWindow(mapView: MapView?): InfoWindow(R.layout.layout_tooltip, mapView) {
    lateinit var binding: LayoutTooltipBinding

    override fun onOpen(item: Any?) {
        binding = LayoutTooltipBinding.bind(view)

//        super.onOpen(item)
        val marker = item as Marker
        val infoWindowData = marker.relatedObject as ModelMain

        val tvNamaLokasi = binding.tvNamaLokasi
        val tvAlamat = binding.tvAlamat
        val imageClose = binding.imageClose

        tvNamaLokasi.text = infoWindowData.strName
        tvAlamat.text = infoWindowData.strVicinity
        imageClose.setOnClickListener {
//            close()
            marker.closeInfoWindow()
        }
    }

    override fun onClose() {
//        super.onClose()
    }
}