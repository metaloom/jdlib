#ifndef handlers_hpp
#define handlers_hpp

#include <stdio.h>
#include <dlib/image_processing/frontal_face_detector.h>
#include <dlib/image_processing.h>
#include <dlib/dnn.h>
using namespace dlib;

// DNN MMOD neural net definition

template <long num_filters, typename SUBNET> using con5d = con<num_filters,5,5,2,2,SUBNET>;
template <long num_filters, typename SUBNET> using con5  = con<num_filters,5,5,1,1,SUBNET>;

template <typename SUBNET> using downsampler  = relu<affine<con5d<32, relu<affine<con5d<32, relu<affine<con5d<16,SUBNET>>>>>>>>>;
template <typename SUBNET> using rcon5  = relu<affine<con5<45,SUBNET>>>;

//typedef loss_mmod<con<1,9,9,1,1,rcon5<rcon5<rcon5<downsampler<input_rgb_image_pyramid<pyramid_down<6>>>>>>>> net_type;
using net_type = loss_mmod<con<1,9,9,1,1,rcon5<rcon5<rcon5<downsampler<input_rgb_image_pyramid<pyramid_down<6>>>>>>>>;

// Neural net definition

template <template <int, template <typename> class, int, typename> class block, int N, template <typename> class BN, typename SUBNET>
using residual = add_prev1<block<N, BN, 1, tag1<SUBNET>>>;

template <template <int, template <typename> class, int, typename> class block, int N, template <typename> class BN, typename SUBNET>
using residual_down = add_prev2<avg_pool<2, 2, 2, 2, skip1<tag2<block<N, BN, 2, tag1<SUBNET>>>>>>;

template <int N, template <typename> class BN, int stride, typename SUBNET>
using block = BN<con<N, 3, 3, 1, 1, relu<BN<con<N, 3, 3, stride, stride, SUBNET>>>>>;

template <int N, typename SUBNET>
using ares = relu<residual<block, N, affine, SUBNET>>;
template <int N, typename SUBNET>
using ares_down = relu<residual_down<block, N, affine, SUBNET>>;

template <typename SUBNET>
using alevel0 = ares_down<256, SUBNET>;
template <typename SUBNET>
using alevel1 = ares<256, ares<256, ares_down<256, SUBNET>>>;
template <typename SUBNET>
using alevel2 = ares<128, ares<128, ares_down<128, SUBNET>>>;
template <typename SUBNET>
using alevel3 = ares<64, ares<64, ares<64, ares_down<64, SUBNET>>>>;
template <typename SUBNET>
using alevel4 = ares<32, ares<32, ares<32, SUBNET>>>;

typedef loss_metric<fc_no_bias<128, avg_pool_everything<
                                        alevel0<
                                            alevel1<
                                                alevel2<
                                                    alevel3<
                                                        alevel4<
                                                            max_pool<3, 3, 2, 2, relu<affine<con<32, 7, 7, 2, 2, input_rgb_image_sized<150>>>>>>>>>>>>>
    anet_type;

class FaceDetectorHandler
{
public:
    static FaceDetectorHandler *getFaceDetectorHandler();
    dlib::frontal_face_detector getFaceDetector();

    FaceDetectorHandler(FaceDetectorHandler const &) = delete;
    void operator=(FaceDetectorHandler const &) = delete;

private:
    FaceDetectorHandler();
    dlib::frontal_face_detector face_detector;
};


class CNNFaceDetectorHandler
{
public:
    static CNNFaceDetectorHandler *getCNNFaceDetectorHandler(const std::string &model_path);

    net_type getCNNFaceDetectorModel();

    CNNFaceDetectorHandler(CNNFaceDetectorHandler const &) = delete;
    void operator=(CNNFaceDetectorHandler const &) = delete;

private:
    CNNFaceDetectorHandler(const std::string &model_path);
    net_type cnn_face_model;
    std::string model_path;
};


class ShapePredictorHandler
{
public:
    static ShapePredictorHandler *getShapePredictorHandler(const std::string &model_path);
    dlib::shape_predictor getShapePredictorModel();

    ShapePredictorHandler(ShapePredictorHandler const &) = delete;
    void operator=(ShapePredictorHandler const &) = delete;

private:
    ShapePredictorHandler(const std::string &model_path);
    dlib::shape_predictor shape_predictor_model;
    std::string model_path;
};

class FaceEmbeddingHandler
{
public:
    static FaceEmbeddingHandler *getFaceEmbeddingHandler(const std::string &model_path);

    anet_type getFaceEmbeddingModel();

    FaceEmbeddingHandler(FaceEmbeddingHandler const &) = delete;
    void operator=(FaceEmbeddingHandler const &) = delete;

private:
    FaceEmbeddingHandler(const std::string &model_path);
    anet_type face_embedding_model;
    std::string model_path;
};

#endif /* handlers_hpp */
