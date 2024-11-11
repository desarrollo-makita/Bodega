package com.makita.ubiapp.ui.util

import android.util.Log
import com.makita.ubiapp.PickingDetalleItem

fun procesarTextoEscaneado(
    texto: String,
    pickingList: MutableList<PickingDetalleItem>?,
    onError: (String) -> Unit): Boolean {

    Log.d("*MAKITA*", "Ingreso funcion procesarTextoEscaneado  $texto")
    Log.d("*MAKITA*", "El largo dle texto es ${texto.length}")
    Log.d("*MAKITA*", "Listado de item detalle $pickingList")

    val largoTexto = texto.length
    //val itemScanner = texto.substring(0, 20).trim()
    val itemScanner = "M9204B"
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
            if (itemDetalle.Cantidad < itemDetalle.CantidadPedida) {
                // Solo sumamos si la cantidad es menor que la cantidad pedida
                itemDetalle.Cantidad += 1
                Log.d("*MAKITA*", "Cantidad actualizada a ${itemDetalle.Cantidad}")

                // Aquí no es necesario reasignar la lista, ya que la estamos modificando directamente
                return true
            } else {
                Log.d("*MAKITA*", "La cantidad ya alcanzó la cantidad pedida.")
                onError("La cantidad para el item ${itemDetalle.item} ya alcanzó la cantidad pedida.")
            }
        }else {
            // Si el item no se encuentra
            Log.d("*MAKITA*", "Item no encontrado")
            onError("Item no encontrado en la lista")
        }
    }

    return false
}
