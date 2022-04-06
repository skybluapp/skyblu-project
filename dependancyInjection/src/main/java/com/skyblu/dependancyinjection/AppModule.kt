package com.skyblu.configuration

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.room.Room
import com.skyblu.data.authentication.FirebaseAuthentication
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DataStoreRepository
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.firestore.FireStore
import com.skyblu.data.firestore.ServerInterface
import com.skyblu.data.room.AppDatabase
import com.skyblu.data.room.TrackingPointsDao
import com.skyblu.data.storage.FirebaseStorage
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedUsers
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.jumptracker.service.SkybluAppService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import javax.inject.Named
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("test_string")
    fun provideTestString() = "This is a test string"

    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return appContext.dataStore
    }

    @Singleton
    @Provides
    @Named("AppContext")
    fun provideApplicationContext(@ApplicationContext applicationContext: Context): Context {
        return applicationContext
    }

    @Singleton
    @Provides
    fun provideService(@ApplicationContext activityContext: Context): SkybluAppService {
        return SkybluAppService(context = activityContext)
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): TrackingPointsDao {
        return database.trackingPointsDao()
    }

    @Composable
    @Singleton
    @Provides
    fun provideNavHostController(@ApplicationContext context: Context): NavHostController {
        return NavHostController(context)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "DATABASE",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthentication() : FirebaseAuthentication{
        return FirebaseAuthentication()
    }

    @Singleton
    @Provides
    fun provideFirestore() : FireStore{
        return FireStore()
    }

    @Singleton
    @Provides
    fun provideStorage() : FirebaseStorage{
        return FirebaseStorage()
    }

    @Singleton
    @Provides
    fun provideSavedUsers(authentication : AuthenticationInterface) : SavedUsers{
        return SavedUsers(authentication)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindings {

    @Singleton
    @Binds
    abstract fun bindingFunction(dataStoreRepository: DataStoreRepository): DatastoreInterface

    @Singleton
    @Binds
    abstract fun firestoreInterface(fireStore: FireStore): ServerInterface

    @Singleton
    @Binds
    abstract fun firebaseAuthentication(firebaseAuthentication: FirebaseAuthentication): AuthenticationInterface

    @Singleton
    @Binds
    abstract fun clientToServiceInterface(cTsInterface: SkybluAppService): ClientToService

    @Singleton
    @Binds
    abstract fun firebaseStorage(firebaseStorage : FirebaseStorage) : StorageInterface

    @Singleton
    @Binds
    abstract fun savedUsers(savedUsers : SavedUsers) : SavedUsersInterface

}




























@ActivityScoped
interface PermissionsInterface {

    fun requestPermission(vararg permissions: String)
    fun checkPermissions(vararg permissions: String): Boolean
}

@ActivityScoped
class PermissionsInterfaceImpl(private val activity: Activity) : PermissionsInterface {

    override fun requestPermission(vararg permissions: String) {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                activity,
                1,
                *permissions
            )
                .build(),
        )
    }

    override fun checkPermissions(vararg permissions: String): Boolean {
        return EasyPermissions.hasPermissions(
            activity,
            *permissions
        )
    }
}

