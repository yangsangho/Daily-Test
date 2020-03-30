package kr.yangbob.memorization.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kr.yangbob.memorization.R
import kr.yangbob.memorization.SortInfo
import kr.yangbob.memorization.databinding.LayoutSortDialogBinding

class SortDialog(context: Context, sortInfo: SortInfo, sortItemName: List<String>) {
    private val dialogBinding: LayoutSortDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_sort_dialog, null, false)
    private val sortDialog: AlertDialog
    private val sortInfo = SortInfo(sortInfo.sortedItemIdx, sortInfo.isAscending)
    private val changeObserver = MutableLiveData<SortInfo>()

    init {
        dialogBinding.sortDialog = this
        dialogBinding.sortItemName = sortItemName
        sortDialog = AlertDialog.Builder(context).apply {
            setView(dialogBinding.root)
        }.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    fun show() {
        dialogBinding.sortInfo = sortInfo
        sortDialog.show()
    }

    fun cancelDialog() {
        sortDialog.dismiss()
    }

    fun doneDialog() {
        val sortIdx = when (dialogBinding.sortItemGroup.checkedRadioButtonId) {
            R.id.sortItem1 -> 0
            R.id.sortItem2 -> 1
            R.id.sortItem3 -> 2
            else -> -1
        }
        val order = when (dialogBinding.sortOrderGroup.checkedRadioButtonId) {
            R.id.sortAscending -> true
            R.id.sortDescending -> false
            else -> null
        }
        if (sortIdx == -1 || order == null) throw IllegalArgumentException()
        if (sortIdx != sortInfo.sortedItemIdx || order != sortInfo.isAscending) {
            sortInfo.sortedItemIdx = sortIdx
            sortInfo.isAscending = order
            changeObserver.value = sortInfo
        }
        cancelDialog()
    }

    fun getChangeObserver(): LiveData<SortInfo> = changeObserver
}