/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.model

import android.net.Uri

/**
 * A PNG image file object consisting in its URI and name.
 */
data class Picture(val uri: Uri, val name: String)
