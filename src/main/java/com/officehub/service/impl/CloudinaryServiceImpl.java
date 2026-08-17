package com.officehub.service.impl;


import java.io.IOException;

import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.officehub.service.CloudinaryService;



@Service
public class CloudinaryServiceImpl implements CloudinaryService {


    private final Cloudinary cloudinary;


    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }



    @Override
    public String uploadImage(MultipartFile file) {

        try {

            Map uploadResult =
                    cloudinary.uploader()
                    .upload(
                        file.getBytes(),
                        ObjectUtils.emptyMap()
                    );


            return uploadResult
                    .get("secure_url")
                    .toString();


        } catch (IOException e) {

            throw new RuntimeException(
                    "Image upload failed"
            );
        }

    }

}