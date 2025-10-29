package com.example.iptvreader.parser

import com.example.iptvreader.model.IPTVItem

object M3UParser {

    private val attrRegex = Regex("""([a-zA-Z0-9\-]+)\s*=\s*"([^"]*)"""")

    fun parse(m3uText: String): List<IPTVItem> {
        val lines = m3uText.lines().map { it.trim() }
        val items = mutableListOf<IPTVItem>()

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                val commaIndex = line.indexOf(',')
                val infoPart = if (commaIndex >= 0) line.substring(0, commaIndex) else line
                val titlePart = if (commaIndex >= 0) line.substring(commaIndex + 1).trim() else ""

                val duration = run {
                    val after = infoPart.removePrefix("#EXTINF:").trim()
                    val parts = after.split("\\s+".toRegex(), limit = 2)
                    parts.getOrNull(0)?.toIntOrNull() ?: -1
                }

                val attrs = mutableMapOf<String, String>()
                attrRegex.findAll(infoPart).forEach { match ->
                    val key = match.groups[1]?.value ?: ""
                    val value = match.groups[2]?.value ?: ""
                    attrs[key] = value
                }

                var urlLine: String? = null
                var j = i + 1
                while (j < lines.size) {
                    val candidate = lines[j]
                    if (candidate.isNotEmpty() && !candidate.startsWith("#")) {
                        urlLine = candidate
                        break
                    }
                    j++
                }

                if (!urlLine.isNullOrEmpty()) {
                    val item = IPTVItem(
                        title = if (titlePart.isNotEmpty()) titlePart else attrs["tvg-name"] ?: urlLine,
                        url = urlLine,
                        duration = duration,
                        tvgId = attrs["tvg-id"],
                        tvgName = attrs["tvg-name"],
                        tvgLogo = attrs["tvg-logo"],
                        groupTitle = attrs["group-title"]
                    )
                    items.add(item)
                }

                i = if (j > i) j else i + 1
            } else {
                i++
            }
        }

        return items
    }
}