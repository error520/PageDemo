package com.kinco.MotorApp.ui.fourthpage;

public class function {
}
//        showSurfaceView.setOnTouchListener(new View.OnTouchListener()
//        {
//
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                SurfaceView imageView = (SurfaceView) view;
//                switch (event.getAction()&MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        savedMatrix.set(matrix);
//                        start.set(event.getX(), event.getY());
//                        mode = MOVE;
//                        rotate = NONE;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_POINTER_UP:
//                        mode = NONE;
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN:
//                        oldDistance = (float)Math.sqrt((event.getX(0)-event.getX(1))*(event.getX(0)-event.getX(1))+(event.getY(0)-event.getY(1))*(event.getY(0)-event.getY(1)));
//                        if (oldDistance > 10f) {
//                            savedMatrix.set(matrix);
//                            mid.set((event.getX(0)+event.getX(1))/2, (event.getY(0)+event.getY(1))/2);
//                            mode = ZOOM;
//                        }
//                    case MotionEvent.ACTION_MOVE:
//                        if (mode == MOVE)
//                        {
//                            if(rotate == NONE) {
//                                savedMatrix.set(matrix);
//                                mid.set(event.getX(), event.getY());
//                                rotate = ROTATION;
//                            }
//                            else {
//                                matrix.set(savedMatrix);
//                                double a = Math.atan((mid.y-start.y)/(mid.x-start.x));
//                                double b = Math.atan((event.getY()-mid.y)/(event.getX()-mid.x));
//                                if ((b - a < Math.PI/2 && b - a > Math.PI / 18)||((b + Math.PI) % Math.PI - a < Math.PI/2 && (b + Math.PI) % Math.PI - a > Math.PI / 18)) {
//                                    matrix.postScale((float)0.9, (float)0.9);
//                                }
//                                else if ((a - b < Math.PI / 2 && a - b > Math.PI / 18)||((a + Math.PI) % Math.PI - b < Math.PI/2 && (a + Math.PI) % Math.PI - b > Math.PI / 18)) {
//                                    matrix.postScale((float)1.1, (float)1.1);
//                                }
//                                start.set(event.getX(), event.getY());
//                                rotate = NONE;
//                            }
//                        }
//                        else if(mode == ZOOM)
//                        {
//                            float newDistance;
//                            newDistance = (float)Math.sqrt((event.getX(0)-event.getX(1))*(event.getX(0)-event.getX(1))+(event.getY(0)-event.getY(1))*(event.getY(0)-event.getY(1)));
//                            if(newDistance > 10f) {
//                                matrix.set(savedMatrix);
//                                matrix.postScale(newDistance/oldDistance, newDistance/oldDistance, mid.x, mid.y);
//                                oldDistance = newDistance;
//                                savedMatrix.set(matrix);
//                            }
//                        }
//                        break;
//                }
//                imageView.
//                imageView.setImageMatrix(matrix);
//                return true;
//            }
//
//        });

