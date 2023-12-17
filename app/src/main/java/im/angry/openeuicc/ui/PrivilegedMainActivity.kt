package im.angry.openeuicc.ui

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import im.angry.openeuicc.R
import im.angry.openeuicc.core.EuiccChannel
import im.angry.openeuicc.util.*

class PrivilegedMainActivity : MainActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_main_privileged, menu)

        if (tm.supportsDSDS) {
            val dsds = menu.findItem(R.id.dsds)
            dsds.isVisible = true
            dsds.isChecked = tm.dsdsEnabled
        }

        return true
    }

    internal fun showSlotMappingFragment() =
        SlotMappingFragment().show(supportFragmentManager, SlotMappingFragment.TAG)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.dsds -> {
            tm.dsdsEnabled = !item.isChecked
            Toast.makeText(this, R.string.toast_dsds_switched, Toast.LENGTH_LONG).show()
            finish()
            true
        }
        R.id.slot_mapping -> {
            showSlotMappingFragment()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun createEuiccManagementFragment(channel: EuiccChannel): EuiccManagementFragment =
        PrivilegedEuiccManagementFragment.newInstance(channel.slotId, channel.portId)
}