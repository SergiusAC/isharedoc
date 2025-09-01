# Build native app via GraalVM
FROM vegardit/graalvm-maven:latest-java21 AS build-image
WORKDIR "/task"
COPY . ./
RUN mvn --no-transfer-progress -B -Pnative native:compile -DskipTests

FROM public.ecr.aws/amazonlinux/amazonlinux:2023
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:0.9.1 /lambda-adapter /opt/extensions/lambda-adapter
WORKDIR /opt
COPY --from=build-image /task/target/isharedoc-api /opt/app
CMD ["./app"]