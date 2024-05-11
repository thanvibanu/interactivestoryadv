package com.app.readbook.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import com.app.readbook.App
import com.app.readbook.App.Companion.user
import com.app.readbook.base.BaseVBFragment
import com.app.readbook.databinding.DialogEditPasswordBinding
import com.app.readbook.databinding.FragmentMineBinding
import com.app.readbook.ui.activity.FlowListActivity
import com.app.readbook.ui.activity.LoginActivity
import com.app.readbook.ui.activity.UserInfoActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore


class MineFragment : BaseVBFragment<FragmentMineBinding>() {
    override fun initView() {
        if (user == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        if (user?.type == "Reader") {
            binding.llFlow.visibility = View.VISIBLE
        }
        binding.llInfo.setOnClickListener {
            startActivity(Intent(requireContext(), UserInfoActivity::class.java))
        }
    }

    private fun showEditPasswordDialog() {
        val binding = DialogEditPasswordBinding.inflate(
            layoutInflater
        )
        val alertDialog = AlertDialog.Builder(activity).setView(binding.root).show()
        binding.tvSure.setOnClickListener { v: View? ->
            val originPassword = binding.etOriginPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            if (originPassword != user?.password) {
                toast("Original password error")
                return@setOnClickListener
            }
            if (newPassword.isEmpty()) return@setOnClickListener
            user?.password = newPassword
            user?.id?.let {
                FirebaseFirestore.getInstance().collection("User").document(it).set(user!!)
                    .addOnSuccessListener(OnSuccessListener {
                        toast("Modification succeeded")
                        alertDialog.dismiss()
                    })
            }
        }
        binding.tvCancel.setOnClickListener { alertDialog.dismiss() }
    }

    override fun initData() {
        binding.llEditPassword.setOnClickListener { v: View? -> showEditPasswordDialog() }
        binding.llOut.setOnClickListener { v: View? ->
            App.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.llFlow.setOnClickListener {
            startActivity(Intent(requireContext(), FlowListActivity::class.java))
        }
    }
}



