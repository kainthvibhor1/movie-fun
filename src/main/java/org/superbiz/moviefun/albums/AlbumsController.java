package org.superbiz.moviefun.albums;

import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private BlobStore store;

    public AlbumsController(AlbumsBean albumsBean, BlobStore store) {
        this.store = store;
        this.albumsBean = albumsBean;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob inputBlob = new Blob(formatBlobName(albumId),
                uploadedFile.getInputStream(),
                uploadedFile.getContentType());
        this.store.put(inputBlob);
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> blobOptional = this.store.get(formatBlobName(albumId));
        if (!blobOptional.isPresent()) {
            blobOptional = this.getDefaultBlob();
        }
        Blob blob = blobOptional.get();
        byte[] imageBytes = IOUtils.toByteArray(blob.inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(blob.contentType));
        headers.setContentLength(imageBytes.length);

        return new HttpEntity<>(imageBytes, headers);
    }

    private Optional<Blob> getDefaultBlob() {
        ClassLoader loader = AlbumsController.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream("default-cover.jpg");
        Blob defaultVal = new Blob("cover/default-cover", stream, MediaType.IMAGE_JPEG_VALUE);
        return Optional.of(defaultVal);
    }

    private String formatBlobName(Long albumId) {
        return format("covers/%d", albumId);
    }
}
