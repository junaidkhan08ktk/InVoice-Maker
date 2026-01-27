import SwiftUI
import composeApp

@main
struct iosApp: App {
    init() {
        MainViewControllerKt.initKoinIOS()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
