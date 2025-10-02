androidApplication {
    namespace = "org.example.app"

    dependencies {
        // AppCompat, Material, and ConstraintLayout for modern views and styling
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.gridlayout:gridlayout:1.0.0")

        // Remove unused sample deps and internal modules
        // implementation("org.apache.commons:commons-text:1.11.0")
        // implementation(project(":utilities"))
    }
}
