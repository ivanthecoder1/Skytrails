import React, { useState } from 'react';
import SkytrailImage from "../images/SkytrailImage.jpg";
import "./ImageContainer.css";


const ImageContainer = () => {
  const [imageUrl, setImageUrl] = useState('');

  const handleImageUpload = (event) => {
    const file = event.target.files[0];

    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImageUrl(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="image-container">
      {imageUrl && (
        <div className="image-wrapper">
          <img src={imageUrl} className="centered-image" />
        </div>
      )}
    </div>
  );
};

export default ImageContainer;
