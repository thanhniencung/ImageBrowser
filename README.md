# Short demo
<h1><a href="https://youtu.be/SnP-DoaCOjs">Youtube Video</a></h1>

# STEP 1: When user click on the item of the RecycleView 
```java
ImageDialogFragment.showImages(context.getSupportFragmentManager(), getAdapterPosition(), imageView, imageList);
```

# STEP 2: Handle RecycleView every onPageSelected called
```java
 @Override
protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
}

@Subscribe(threadMode = ThreadMode.MAIN)
public void onPhotoChange(final PhotoChange photoChange) {
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            gridLayoutManager.scrollToPosition(photoChange.position);
        }
    }, 250);

    // wait recycleview scrolled and then we need to update position on the screen for the current image
    // and this code should be improved, please make a pull request :D 
    
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            View view = gridLayoutManager.findViewByPosition(photoChange.position);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            if (imageView == null) {
                return;
            }
            float[] initMatrixData = new float[9];
            Matrix initMatrixImageView = MatrixUtils.getImageMatrix(imageView);
            initMatrixImageView.getValues(initMatrixData);

            int[] screenLocation = new int[2];
            view.getLocationInWindow(screenLocation);

            PhotoLocation photoLocation = new PhotoLocation();
            photoLocation.position = photoChange.position;
            photoLocation.top = screenLocation[1];
            photoLocation.left = screenLocation[0];
            photoLocation.matrixData = initMatrixData;

            EventBus.getDefault().post(photoLocation);
        }
    }, 500);
}
```
