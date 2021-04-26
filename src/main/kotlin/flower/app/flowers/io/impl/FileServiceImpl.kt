package flower.app.flowers.io.impl

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileServiceImpl : FileService {

    companion object {
        //public static final String filePath = "usr/local/tomcat/webapps/upload";
        const val filePath = "upload"
    }

    private val rootLocation = Paths.get(Objects.requireNonNull(filePath))

    override fun store(file: MultipartFile): String {
        if (file.isEmpty || file.originalFilename == null || file.originalFilename!!.isEmpty()) {
            return ""
        }
        if (!Files.exists(rootLocation)) {
            Files.createDirectory(rootLocation)
        }
        return createFileName(file, rootLocation)
    }

    override fun getThumbnailFromVideo(fileName: String): String {
        val outputFilePrefix = UUID.randomUUID().toString()
        val outputdir = File(rootLocation.toString())
        val timestamps: MutableList<Long> = ArrayList()
        timestamps.add(1L)
        MediaSnapshotCreator.makeSnapshots(
                fileName,
                timestamps,
                outputdir,
                outputFilePrefix,
                640,
                480
        )
        return "$outputFilePrefix.jpg"
    }

    override fun storeToTemp(file: MultipartFile): String {
        val rootLocation = Paths.get(Objects.requireNonNull("/tmp"))
        return createFileName(file, rootLocation)
    }

    private fun createFileName(file: MultipartFile, rootLocation: Path): String {
        val filename = UUID.randomUUID().toString().plus(file.originalFilename)
        Files.copy(file.inputStream, rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING)
        return filename
    }

    override fun storeFromTempToLocalDirectory(fileName: String, directoryName: String): String {
        return try {
            val directoryLocation = Paths.get(Objects.requireNonNull(directoryName))
            if (!Files.exists(directoryLocation)) {
                Files.createDirectory(directoryLocation)
            }
            val file = File("/tmp/$fileName")
            if (!file.exists()) return ""
            Files.copy(file.toPath(), directoryLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            e.message!!
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    @Throws(MalformedURLException::class)
    override fun loadAsResource(filename: String): Resource {
        val path = load(filename)
        return UrlResource(path.toUri())
    }

    @Throws(IOException::class)
    override fun deleteByName(filename: String): Boolean {
        return FileSystemUtils.deleteRecursively(load(filename))
    }
}
