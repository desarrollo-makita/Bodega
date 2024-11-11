package com.makita.ubiapp.ui.util

import android.util.Log
import com.makita.ubiapp.PickingDetalleItem

fun procesarTextoEscaneado(texto: String, pickingList: List<PickingDetalleItem>?): Boolean {

    Log.d("*MAKITA*", "Ingreso funcion procesarTextoEscaneado  $texto")
    Log.d("*MAKITA*", "El largo dle texto es ${texto.length}")
    Log.d("*MAKITA*", "Listado de item detalle $pickingList")

    val largoTexto = texto.length
    //val itemScanner = texto.substring(0, 20).trim()
    val itemScanner = "DUA301Z"
    val serieInicio = texto.substring(20,29).trim()
    val serieFinal = texto.substring(29,38).trim()
    val letraFabrica= texto.substring(38,39).trim()
    val ean  = texto.substring(39,52).trim()

    // Validar que la data escaneada sea la misma de la tabla detalle
    if (pickingList != null) {

        var itemDetalle = pickingList.find { it.item == itemScanner }

        Log.d("*MAKITA*", "ITEM ENCONTRADO $itemDetalle")

        // Si el item es encontrado, proceder
        if (itemDetalle != null) {

            itemDetalle.Cantidad += 1
            return itemDetalle.Cantidad == itemDetalle.CantidadPedida

        }
    }

    return false
}
