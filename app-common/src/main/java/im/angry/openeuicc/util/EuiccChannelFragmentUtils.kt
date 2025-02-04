package im.angry.openeuicc.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import im.angry.openeuicc.core.EuiccChannel
import im.angry.openeuicc.core.EuiccChannelManager
import im.angry.openeuicc.service.EuiccChannelManagerService
import im.angry.openeuicc.ui.BaseEuiccAccessActivity

interface EuiccChannelFragmentMarker: OpenEuiccContextMarker

// We must use extension functions because there is no way to add bounds to the type of "self"
// in the definition of an interface, so the only way is to limit where the extension functions
// can be applied.
fun <T> newInstanceEuicc(clazz: Class<T>, slotId: Int, portId: Int, addArguments: Bundle.() -> Unit = {}): T where T: Fragment, T: EuiccChannelFragmentMarker {
    val instance = clazz.newInstance()
    instance.arguments = Bundle().apply {
        putInt("slotId", slotId)
        putInt("portId", portId)
        addArguments()
    }
    return instance
}

// Convenient methods to avoid using `channel` for these
// `channel` requires that the channel actually exists in EuiccChannelManager, which is
// not always the case during operations such as switching
val <T> T.slotId: Int where T: Fragment, T: EuiccChannelFragmentMarker
    get() = requireArguments().getInt("slotId")
val <T> T.portId: Int where T: Fragment, T: EuiccChannelFragmentMarker
    get() = requireArguments().getInt("portId")
val <T> T.isUsb: Boolean where T: Fragment, T: EuiccChannelFragmentMarker
    get() = requireArguments().getInt("slotId") == EuiccChannelManager.USB_CHANNEL_ID

val <T> T.euiccChannelManager: EuiccChannelManager where T: Fragment, T: OpenEuiccContextMarker
    get() = (requireActivity() as BaseEuiccAccessActivity).euiccChannelManager
val <T> T.euiccChannelManagerService: EuiccChannelManagerService where T: Fragment, T: OpenEuiccContextMarker
    get() = (requireActivity() as BaseEuiccAccessActivity).euiccChannelManagerService

suspend fun <T, R> T.withEuiccChannel(fn: suspend (EuiccChannel) -> R): R where T : Fragment, T : EuiccChannelFragmentMarker {
    ensureEuiccChannelManager()
    return euiccChannelManager.withEuiccChannel(slotId, portId, fn)
}

suspend fun <T> T.ensureEuiccChannelManager() where T: Fragment, T: OpenEuiccContextMarker =
    (requireActivity() as BaseEuiccAccessActivity).euiccChannelManagerLoaded.await()

interface EuiccProfilesChangedListener {
    fun onEuiccProfilesChanged()
}