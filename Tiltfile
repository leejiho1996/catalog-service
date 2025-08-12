# Build
custom_build(
    ref = 'catalog-service',
    command = 'gradlew bootBuildImage --imageName %EXPECTED_REF%',
    # 새로운 빌드를 시작하기 위해 지켜봐야 하는 파일
    deps = ['build.gradle', 'src']
)

# Deploy
k8s_yaml(kustomize('k8s')) # k8s 폴더에 있는 Kustomize 리소스로 애플리케이션 실행

# Manage
k8s_resource('catalog-service', port_forwards=['9001'])